package com.example.triply.core.booking.entity.hotel;

import com.example.triply.common.audit.Auditable;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.booking.entity.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Hotel_Booking")
@Audited
public class HotelBooking extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_room_type_id", nullable = false)
    private HotelRoomType hotelRoomType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "check_in", nullable = false)
    private LocalDateTime checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDateTime checkOut;

}
