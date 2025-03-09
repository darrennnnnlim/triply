package com.example.triply.core.ratings.entity;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Ratings")
@Audited
@Getter
@Setter
@NoArgsConstructor
public class Ratings extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private Long flightHotelId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getFlightHotelId() {
        return flightHotelId;
    }

    public void setFlightHotelId(Long flightHotelId) {
        this.flightHotelId = flightHotelId;
    }


}
