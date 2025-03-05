package com.example.triply.core.booking.entity.hotel;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "Hotel_Booking_Addon")
@Audited
public class HotelBookingAddon extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_booking_id", nullable = false)
    private HotelBooking hotelBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_addon_id", nullable = false)
    private HotelAddon hotelAddon;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
}
