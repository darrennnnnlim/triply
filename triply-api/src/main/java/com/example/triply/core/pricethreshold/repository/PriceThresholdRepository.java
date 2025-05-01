package com.example.triply.core.pricethreshold.repository;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.pricethreshold.entity.PriceThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PriceThresholdRepository extends JpaRepository<PriceThreshold, Long> {

    /**
     * Finds all price thresholds associated with a specific user.
     *
     * @param user The user entity.
     * @return A list of price thresholds for the given user.
     */
    List<PriceThreshold> findByUser(User user);

    /**
     * Finds all price thresholds for a specific flight where the threshold price
     * is less than or equal to the given price.
     *
     * @param flight The flight entity.
     * @param price  The price to compare against.
     * @return A list of matching price thresholds.
     */
    List<PriceThreshold> findByConceptTypeAndConceptIdAndThresholdPriceLessThanEqual(String conceptType, Long conceptId, BigDecimal price);

    /**
     * Finds all price thresholds for a specific hotel where the threshold price
     * is less than or equal to the given price.
     *
     * @param hotel The hotel entity.
     * @param price The price to compare against.
     * @return A list of matching price thresholds.
     */
    //List<PriceThreshold> findByHotelAndThresholdPriceLessThanEqual(Hotel hotel, BigDecimal price);

}