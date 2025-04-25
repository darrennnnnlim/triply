package com.example.triply.core.booking.listener;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.event.BookingConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventListener.class);
    private final EmailService emailService;

    @Autowired
    public BookingEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleBookingConfirmedEvent(BookingConfirmedEvent event) {
        Booking booking = event.getBooking();
        LOGGER.info("Received BookingConfirmedEvent for booking ID: {}", booking.getId());

        if (booking != null && booking.getUser() != null && booking.getStatus().equals(BookingStatusEnum.PENDING.name())) {
            try {
                String userEmail = booking.getUser().getEmail();
                emailService.sendBookingConfirmationEmail(userEmail, booking);
                LOGGER.info("Booking confirmation email initiated via event listener for booking ID: {}", booking.getId());
            } catch (Exception e) {
                LOGGER.error("Failed to send booking confirmation email via event listener for booking ID: {}", booking.getId(), e);
            }
        } else {
            LOGGER.warn("Skipping booking confirmation email via event listener for booking ID: {} due to null data or non-PENDING status.", 
                booking != null ? booking.getId() : "null");
        }
    }
}