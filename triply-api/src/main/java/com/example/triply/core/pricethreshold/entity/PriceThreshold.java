package com.example.triply.core.pricethreshold.entity;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.hotel.model.entity.Hotel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "price_thresholds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id") // Nullable if threshold can be for hotel only
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id") // Nullable if threshold can be for flight only
    private Hotel hotel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdPrice;

    // Ensure that either flight or hotel is set, but not both (or handle this logic in service layer)
    // Consider adding constraints or validation logic if needed
}