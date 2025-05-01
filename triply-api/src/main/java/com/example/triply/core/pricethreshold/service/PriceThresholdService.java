package com.example.triply.core.pricethreshold.service;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.pricethreshold.dto.CreatePriceThresholdRequest;
import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PriceThresholdService {

    /**
     * Creates a new price threshold for the specified user.
     *
     * @param request  The request containing threshold details.
     * @param username The username of the user creating the threshold.
     * @return The created PriceThresholdDTO.
     */
    PriceThresholdDTO createThreshold(CreatePriceThresholdRequest request, String username);

    /**
     * Retrieves all price thresholds for the specified user.
     *
     * @param username The username of the user.
     * @return A list of PriceThresholdDTOs.
     */
    List<PriceThresholdDTO> getThresholdsByUser(String username);

    /**
     * Deletes a specific price threshold. Ensures the user owns the threshold.
     *
     * @param thresholdId The ID of the threshold to delete.
     * @param username    The username of the user requesting deletion.
     */
    void deleteThreshold(Long thresholdId, String username);

    /**
     * Finds users whose flight price thresholds are met by a new price.
     *
     * @param flight   The flight entity.
     * @param newPrice The new price of the flight.
     * @return A list of User entities to notify.
     */
    List<User> findUsersToNotifyForFlight(Flight flight, BigDecimal newPrice);

    /**
     * Finds users whose hotel price thresholds are met by a new price.
     *
     * @param hotel    The hotel entity.
     * @param newPrice The new price of the hotel.
     * @return A list of User entities to notify.
     */
    List<User> findUsersToNotifyForHotel(Hotel hotel, BigDecimal newPrice);
}