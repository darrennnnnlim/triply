package com.example.triply.core.booking.dto;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class HotelResponse {
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String location;

    @NotNull
    private boolean availability;

    @NotNull
    private BigDecimal basePrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }


}
