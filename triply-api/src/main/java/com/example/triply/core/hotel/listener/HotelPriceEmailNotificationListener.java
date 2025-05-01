package com.example.triply.core.hotel.listener;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.hotel.event.HotelPriceWriteEvent;
import com.example.triply.core.hotel.model.dto.HotelPriceDTO;
import com.example.triply.core.hotel.repository.HotelRepository; // Added
import com.example.triply.core.pricethreshold.service.PriceThresholdService; // Added
import com.example.triply.core.auth.entity.User; // Added
import com.example.triply.core.hotel.model.entity.Hotel; // Added
import lombok.RequiredArgsConstructor; // Added
import lombok.extern.slf4j.Slf4j; // Added
import org.springframework.scheduling.annotation.Async; // Added
import org.springframework.stereotype.Component;

import java.math.BigDecimal; // Added
import java.util.List;
import java.util.Map; // Added
import java.util.Optional; // Added
import java.util.function.Function; // Added
import java.util.stream.Collectors;

@Slf4j // Changed from Logger
@Component
@RequiredArgsConstructor // Added
public class HotelPriceEmailNotificationListener implements HotelPriceListener {

    // private static final Logger logger = LoggerFactory.getLogger(HotelPriceEmailNotificationListener.class); // Removed
    private final EmailService emailService;
    // Removed old constructor and placeholder fields
    private final PriceThresholdService priceThresholdService; // Added
    private final HotelRepository hotelRepository; // Added


    @Override
    @Async // Added
    public void onPriceUpdate(HotelPriceWriteEvent event) {
        log.info("Received hotel price update event. Processing notifications..."); // Changed logger to log
        List<HotelPriceDTO> oldPrices = event.getOldHotelPrices();
        List<HotelPriceDTO> newPrices = event.getNewHotelPrices();

        // Create a map for quick lookup of old prices by Hotel ID
        // Assuming one price entry per hotel in the event for simplicity, adjust if multiple room types/dates are possible per event
        Map<Long, HotelPriceDTO> oldPriceMap = oldPrices.stream()
                .collect(Collectors.toMap(HotelPriceDTO::getHotelId, Function.identity(), (existing, replacement) -> existing)); // Handle potential duplicates if needed

        for (HotelPriceDTO newPrice : newPrices) {
            HotelPriceDTO oldPrice = oldPriceMap.get(newPrice.getHotelId());

            if (oldPrice != null) {
                // Compare prices (assuming getPrice() returns the relevant comparable price)
                BigDecimal oldEffectivePrice = oldPrice.getPrice(); // Adjust if calculation needed
                BigDecimal newEffectivePrice = newPrice.getPrice(); // Adjust if calculation needed

                // Notify if the price has decreased
                if (newEffectivePrice.compareTo(oldEffectivePrice) < 0) {
                    log.info("Price decreased for hotel {} (ID: {}): {} -> {}",
                            newPrice.getHotelName(), newPrice.getHotelId(), oldEffectivePrice, newEffectivePrice);

                    // Fetch the Hotel entity - needed for the service call
                    Optional<Hotel> hotelOpt = hotelRepository.findById(newPrice.getHotelId());

                    if (hotelOpt.isPresent()) {
                        Hotel hotel = hotelOpt.get();
                        // Find users whose thresholds are met by the new price
                        List<User> usersToNotify = priceThresholdService.findUsersToNotifyForHotel(hotel, newEffectivePrice);

                        log.info("Found {} users to notify for hotel price drop.", usersToNotify.size());
                        for (User user : usersToNotify) {
                            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                                String subject = String.format("🏨 Price Drop Alert! Hotel %s", newPrice.getHotelName());
                                String bodyTemplate = "Hi %s,\n\nThe price for hotel %s has dropped from %.2f %s to %.2f %s!\n\nBook now to grab this deal!\n\nThanks,\nThe Triply Team";
                                String body = String.format(bodyTemplate,
                                        user.getUsername(),
                                        newPrice.getHotelName(),
                                        oldEffectivePrice,
                                        oldPrice.getCurrency(), // Include currency
                                        newEffectivePrice,
                                        newPrice.getCurrency()); // Include currency

                                emailService.sendEmail(user.getEmail(), subject, body); // Called within @Async method
                                log.debug("Queued price drop notification email for user {} to {}", user.getId(), user.getEmail());
                            }
                        }
                    } else {
                        log.warn("Hotel with ID {} not found, cannot notify for price drop.", newPrice.getHotelId());
                    }
                }
            } else {
                log.debug("New price entry detected for hotel ID: {}", newPrice.getHotelId());
            }
        }
        log.info("Finished processing hotel price update notifications.");
    }
}