package com.example.triply.core.pricing.hotel.notification;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HotelRoomTypeNotificationTest implements HotelRoomTypeListener {

    private static final Logger logger = LoggerFactory.getLogger(HotelRoomTypeNotificationTest.class);

    @Override
    public void onPriceUpdate(HotelRoomTypeWriteEvent event) {
        logUpdate(event);
    }

    public void logUpdate(HotelRoomTypeWriteEvent event) {
        logger.error("priceupdated");
        List<HotelRoomTypeDTO> oldHotelRoomTypes = event.getOldHotelRoomTypes();
        if (!oldHotelRoomTypes.isEmpty()) {
            // Updated Hotel Price
            BigDecimal oldBasePrice = oldHotelRoomTypes.get(0).getBasePrice();
            logger.error("Old Base Price: {}", oldBasePrice);
            BigDecimal newBasePrice = event.getNewHotelRoomTypes().get(0).getBasePrice();
            logger.error("New Base Price: {}", newBasePrice);
        } else {
            // Inserted new Hotel Price
            BigDecimal newBasePrice = event.getNewHotelRoomTypes().get(0).getBasePrice();
            logger.error("New Base Price: {}", newBasePrice);
        }

    }
}
