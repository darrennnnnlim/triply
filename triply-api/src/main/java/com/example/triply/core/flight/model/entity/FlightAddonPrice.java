package com.example.triply.core.flight.model.entity;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "Flight_Addon_Price")
@Audited
public class FlightAddonPrice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_addon_id", nullable = false)
    private FlightAddon flightAddon;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
