package com.example.triply.core.pricethreshold.entity;

import com.example.triply.common.audit.Auditable;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.hotel.model.entity.Hotel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_thresholds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceThreshold extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "concept_type", nullable = false)
    private String conceptType; // "FLIGHT" or "HOTEL"

    @Column(name = "concept_id", nullable = false)
    private Long conceptId; // This can be either flightId or hotelId

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "threshold_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdPrice;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id") // Nullable if threshold can be for hotel only
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id") // Nullable if threshold can be for flight only
    private Hotel hotel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdPrice;
    */
}