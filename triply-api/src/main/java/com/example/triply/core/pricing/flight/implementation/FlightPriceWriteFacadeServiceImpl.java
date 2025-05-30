package com.example.triply.core.pricing.flight.implementation;

import com.example.triply.core.flight.mapper.FlightPriceMapper;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.pricing.flight.notification.FlightPriceListener;
import com.example.triply.core.pricing.flight.notification.FlightPriceWritePublisherImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FlightPriceWriteFacadeServiceImpl {

    private final FlightPriceRepository flightPriceRepository;
    private final FlightPriceMapper flightPriceMapper;
    private final FlightPriceWritePublisherImpl publisher; // Use concrete class type

    public FlightPriceWriteFacadeServiceImpl(FlightPriceRepository flightPriceRepository, FlightPriceMapper flightPriceMapper, FlightPriceWritePublisherImpl publisher) { // Use concrete class type
        this.flightPriceRepository = flightPriceRepository;
        this.flightPriceMapper = flightPriceMapper;
        this.publisher = publisher;
    }

    @Transactional
    public List<FlightPriceDTO> updateExistingFlightPrice(Long flightId, Long flightClassId, BigDecimal newBasePrice) {
        Optional<FlightPrice> flightPriceOptional = flightPriceRepository.findByFlightAndFlightClass(flightId, flightClassId);
        List<FlightPriceDTO> oldFlightPrices = new ArrayList<>();
        List<FlightPriceDTO> newFlightPrices = new ArrayList<>();

        if (flightPriceOptional.isPresent()) {
            FlightPrice oldFlightPrice = flightPriceOptional.get();
            oldFlightPrices.add(flightPriceMapper.toDto(oldFlightPrice));

            FlightPrice newFlightPrice = new FlightPrice(oldFlightPrice);
            newFlightPrice.setBasePrice(newBasePrice.setScale(2, RoundingMode.HALF_EVEN));
            newFlightPrices.add(flightPriceMapper.toDto(newFlightPrice));
            flightPriceRepository.save(newFlightPrice);

            publisher.publish(oldFlightPrices, newFlightPrices);
            return newFlightPrices;
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<FlightPriceDTO> insertNewFlightPrice(Long flightId, Long flightClassId,
                                                  BigDecimal newBasePrice, BigDecimal discount,
                                                  BigDecimal surgeMultiplier) {
        FlightPrice newFlightPrice = new FlightPrice();

        // Insert new FlightPrice into Repo
        flightPriceRepository.save(newFlightPrice);

        //Notify
        List<FlightPriceDTO> oldFlightPrices = new ArrayList<>();
        List<FlightPriceDTO> newFlightPrices = new ArrayList<>();
        newFlightPrices.add(flightPriceMapper.toDto(newFlightPrice));
        publisher.publish(oldFlightPrices, newFlightPrices);
        return newFlightPrices;
    }

    public void addPriceListener(FlightPriceListener listener) {
        publisher.addListener(listener);
    }

    public void removePriceListener(FlightPriceListener listener) {
        publisher.removeListener(listener);
    }

}
