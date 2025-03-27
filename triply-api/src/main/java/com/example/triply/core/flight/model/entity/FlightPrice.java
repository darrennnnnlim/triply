package com.example.triply.core.flight.model.entity;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Flight_Price")
@Audited
public class FlightPrice extends Auditable {

    public FlightPrice() {}

    public FlightPrice(FlightPrice other) {
        this.id = other.getId();
        this.flight = other.getFlight();
        this.flightClass = other.getFlightClass();
        this.departureDate = other.getDepartureDate();
        this.basePrice = other.getBasePrice();
        this.discount = other.getDiscount();
        this.surgeMultiplier = other.getSurgeMultiplier();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_class_id", nullable = false)
    private FlightClass flightClass;

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true)
    @DecimalMax(value = "100.00", inclusive = true)
    private BigDecimal discount = BigDecimal.valueOf(1.00);

    @Column(name = "surge_multiplier", nullable = false, precision = 4, scale = 1)
    @DecimalMin(value = "1.0", inclusive = true)
    @DecimalMax(value = "10.0", inclusive = true)
    private BigDecimal surgeMultiplier = BigDecimal.valueOf(1.0);
}
