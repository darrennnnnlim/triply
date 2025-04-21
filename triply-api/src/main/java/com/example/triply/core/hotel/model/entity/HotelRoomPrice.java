package com.example.triply.core.hotel.model.entity;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Hotel_Room_Price")
@Audited
public class HotelRoomPrice extends Auditable {

    public HotelRoomPrice() {}

    public HotelRoomPrice(HotelRoomPrice other) {
        this.id = other.id;
        this.hotelRoomType = other.hotelRoomType;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
        this.price = other.price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_room_type_id", nullable = false)
    private HotelRoomType hotelRoomType;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
