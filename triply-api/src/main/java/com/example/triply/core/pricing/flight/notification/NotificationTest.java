package com.example.triply.core.pricing.flight.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationTest implements FlightPriceListener{

    private static final Logger logger = LoggerFactory.getLogger(NotificationTest.class);

    @Override
    public void onPriceUpdate(FlightPriceWriteEvent event) {
        logUpdate(event);
    }

    public void logUpdate(FlightPriceWriteEvent event) {
        logger.error("priceupdated");
        List<FlightPriceDTO> oldFlightPrices = event.getOldFlightPrices();
        if (!oldFlightPrices.isEmpty()) {
            // Updated Flight Price
            BigDecimal oldBasePrice = oldFlightPrices.get(0).getBasePrice();
            logger.error("Old Base Price: {}", oldBasePrice);
            BigDecimal newBasePrice = event.getNewFlightPrices().get(0).getBasePrice();
            logger.error("New Base Price: {}", newBasePrice);
        } else {
            // Inserted new Flight Price
            BigDecimal newBasePrice = event.getNewFlightPrices().get(0).getBasePrice();
            logger.error("New Base Price: {}", newBasePrice);
        }

    }
}
