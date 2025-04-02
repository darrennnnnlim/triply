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
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
