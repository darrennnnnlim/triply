package com.example.triply.core.pricing.flight.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.pricethreshold.service.PriceThresholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlightPriceEmailNotificationListener implements FlightPriceListener { // Implement custom listener interface

    private final EmailService emailService;
    // private final UserRepository userRepository; // Removed
    private final PriceThresholdService priceThresholdService; // Added

    @Override // Add Override annotation
    @Async // Ensure email sending doesn't block the main thread
    public void onPriceUpdate(FlightPriceWriteEvent event) {
        log.info("Listener received FlightPriceWriteEvent. Processing notifications...");
        log.debug("Event details: Old prices count={}, New prices count={}", event.getOldFlightPrices().size(), event.getNewFlightPrices().size());

        List<FlightPriceDTO> oldPrices = event.getOldFlightPrices(); // Use correct getter from custom event
        List<FlightPriceDTO> newPrices = event.getNewFlightPrices(); // Use correct getter from custom event

        if (newPrices == null || newPrices.isEmpty()) {
            log.warn("No new prices found in the event. Skipping processing.");
            return;
        }

        // Create a map for quick lookup of old prices based on a composite key
        Map<String, FlightPriceDTO> oldPriceMap = oldPrices.stream()
                .collect(Collectors.toMap(this::createPriceKey, Function.identity(), (existing, replacement) -> existing)); // Handle potential duplicates

        for (FlightPriceDTO newPrice : newPrices) {
            String key = createPriceKey(newPrice);
            log.debug("Processing new price with key: {}", key);
            FlightPriceDTO oldPrice = oldPriceMap.get(key);

            if (oldPrice != null) {
                log.debug("Found matching old price for key: {}", key);
                // Compare prices (e.g., notify if the new price is lower)
                BigDecimal oldEffectivePrice = calculateEffectivePrice(oldPrice);
                BigDecimal newEffectivePrice = calculateEffectivePrice(newPrice);
                log.debug("Calculated prices: Old={}, New={}", oldEffectivePrice, newEffectivePrice);

                // Notify if the price has decreased
                if (newEffectivePrice != null && oldEffectivePrice != null && newEffectivePrice.compareTo(oldEffectivePrice) < 0) {
                    log.info("Price decreased for flight {} class {} on {}: {} -> {}",
                            newPrice.getFlightNumber() != null ? newPrice.getFlightNumber() : "N/A", // Use DTO field
                            newPrice.getFlightClassName() != null ? newPrice.getFlightClassName() : "N/A", // Use DTO field
                            newPrice.getDepartureDate(),
                            oldEffectivePrice, newEffectivePrice);

                    // Find users whose thresholds are met by the new price
                    // Note: The service method signature findUsersToNotifyForFlight(Flight, BigDecimal)
                    // doesn't explicitly include flightClass or departureDate.
                    // Assuming the service implementation handles this filtering internally based on the Flight entity.
                    if (newPrice.getFlightDTO() == null || newPrice.getFlightDTO().getId() == null) {
                         log.warn("Cannot find users to notify because flight information is missing in the new price DTO for key: {}", key);
                         continue; // Skip if flight info is missing
                    }
                    log.debug("Querying thresholds for flight ID {} with new price {}", newPrice.getFlightDTO().getId(), newEffectivePrice);
                    // Pass the Flight object, not just the ID
                    List<User> usersToNotify = priceThresholdService.findUsersToNotifyForFlight(newPrice.getFlightDTO().getId(), newEffectivePrice);

                    log.info("Found {} users to notify for flight price drop.", usersToNotify.size());
                    if (usersToNotify != null && !usersToNotify.isEmpty()) { // Added null check
                        for (User user : usersToNotify) {
                            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                                try {
                                    String subject = String.format("✈️ Price Drop Alert! Flight %s", newPrice.getFlightNumber() != null ? newPrice.getFlightNumber() : "N/A"); // Use DTO field
                                    // Consider fetching the user's specific threshold to include in the email
                                    String bodyTemplate = "Hi %s,\n\nThe price for flight %s (%s -> %s) on %s (%s class) has dropped from %.2f to %.2f!\n\nBook now to grab this deal!\n\nThanks,\nThe Triply Team";
                                    String body = String.format(bodyTemplate,
                                            user.getUsername(), // Use username instead of firstName
                                            newPrice.getFlightNumber() != null ? newPrice.getFlightNumber() : "N/A", // Use DTO field
                                            newPrice.getOrigin() != null ? newPrice.getOrigin() : "N/A", // Use DTO field
                                            newPrice.getDestination() != null ? newPrice.getDestination() : "N/A", // Use DTO field
                                            newPrice.getDepartureDate() != null ? newPrice.getDepartureDate().toLocalDate() : "N/A",
                                            newPrice.getFlightClassName() != null ? newPrice.getFlightClassName() : "N/A", // Use DTO field
                                            oldEffectivePrice,
                                            newEffectivePrice);

                                    log.info("Attempting to send price drop email to user {} ({})", user.getId(), user.getEmail());
                                    emailService.sendEmail(user.getEmail(), subject, body); // Called within @Async method
                                    log.info("Successfully queued price drop notification email for user {} to {}", user.getId(), user.getEmail());
                                } catch (Exception e) {
                                    log.error("Failed to send price drop notification email for user {}: {}", user.getId(), e.getMessage(), e);
                                }
                            } else {
                                log.warn("Skipping notification for a user with null data or missing email.");
                            }
                        }
                    }
                } else {
                     log.debug("Price did not decrease or old price not found for key: {}", key);
                }
            } else {
                 log.info("No matching old price found for new price entry with key: {}. Treating as new price.", key);
            }
        }
        log.info("Finished processing flight price update notifications.");
    }

    // Helper method to create a unique key for a flight price entry
    private String createPriceKey(FlightPriceDTO price) {
        // Ensure null safety for components of the key
        String flightId = price.getFlightDTO() != null && price.getFlightDTO().getId() != null ? price.getFlightDTO().getId().toString() : "null_flight";
        String flightClassId = price.getFlightClassDTO() != null && price.getFlightClassDTO().getId() != null ? price.getFlightClassDTO().getId().toString() : "null_class";
        String departureDateStr = price.getDepartureDate() != null ? price.getDepartureDate().toString() : "null_date";

        return String.format("%s-%s-%s", flightId, flightClassId, departureDateStr);
    }

     // Helper method to calculate the effective price considering discounts and surges
    private BigDecimal calculateEffectivePrice(FlightPriceDTO price) {
        BigDecimal base = Objects.requireNonNullElse(price.getBasePrice(), BigDecimal.ZERO);
        BigDecimal discount = Objects.requireNonNullElse(price.getDiscount(), BigDecimal.ONE); // Assume 1.0 (no discount) if null
        BigDecimal surge = Objects.requireNonNullElse(price.getSurgeMultiplier(), BigDecimal.ONE); // Assume 1.0 (no surge) if null

        // Ensure discount and surge are not zero to avoid division issues or invalid prices
         if (discount.compareTo(BigDecimal.ZERO) <= 0) {
             discount = BigDecimal.ONE; // Prevent invalid discount
         }
         if (surge.compareTo(BigDecimal.ZERO) <= 0) {
             surge = BigDecimal.ONE; // Prevent invalid surge
         }


        // Price calculation logic might need adjustment based on exact requirements
        // Example: Price = Base * Surge / Discount (if discount is > 1) or Base * Surge * (1 - discount) if discount is < 1
        // Assuming discount is a factor like 0.9 for 10% off
         // Let's assume a simple model: EffectivePrice = BasePrice * SurgeMultiplier * Discount (where discount < 1 means price reduction)
         // If discount represents a percentage (e.g., 0.1 for 10%), the formula would be Base * Surge * (1 - Discount)
         // Sticking to the simpler model based on field names for now:
        return base.multiply(surge).multiply(discount); // Adjust if discount logic is different
    }
}