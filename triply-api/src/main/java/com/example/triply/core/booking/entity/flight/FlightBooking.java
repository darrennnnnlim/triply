package com.example.triply.core.booking.entity.flight;

import com.example.triply.common.audit.Auditable;
import com.example.triply.core.booking.entity.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Table(name = "Flight_Booking")
@Audited
@NoArgsConstructor
@Getter
@Setter
public class FlightBooking extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private String seatClass;

    @Column(nullable = false)
    private String seatNumber;

}
