package com.example.triply.core.booking.entity.flight;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "Flight_Booking_Addon")
@Audited
public class FlightAddonBooking extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_booking_id", nullable = false)
    private FlightBooking flightBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_addon_id", nullable = false)
    private FlightAddon flightAddon;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 1 CHECK (quantity > 0)")
    @Min(1)
    private int quantity;
}
