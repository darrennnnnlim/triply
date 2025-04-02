package com.example.triply.core.booking.entity.flight;

import com.example.triply.common.audit.Auditable;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Flight_Booking")
@Audited
public class FlightBooking extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_class_id", nullable = false)
    private FlightClass flightClass;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

}
