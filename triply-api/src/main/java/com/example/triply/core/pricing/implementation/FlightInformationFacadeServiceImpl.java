package com.example.triply.core.pricing.implementation;

import com.example.triply.core.search.flight.model.dto.FlightSearchRequestDTO;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.AirlineRepository;
import com.example.triply.core.flight.repository.FlightClassRepository;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.pricing.model.dto.FlightOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightInformationFacadeServiceImpl {

	public static final Logger LOGGER = LoggerFactory.getLogger(FlightInformationFacadeServiceImpl.class);

	private final FlightRepository flightRepository;
	private final FlightPriceRepository flightPriceRepository;
	private final FlightClassRepository flightClassRepository;
	private final AirlineRepository airlineRepository;

    public FlightInformationFacadeServiceImpl(FlightRepository flightRepository, FlightPriceRepository flightPriceRepository, FlightClassRepository flightClassRepository, AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.flightPriceRepository = flightPriceRepository;
        this.flightClassRepository = flightClassRepository;
        this.airlineRepository = airlineRepository;
    }

    public List<FlightOfferDTO> getFlightPrices(FlightSearchRequestDTO flightSearchRequestDTO) {
		String originIATA = flightSearchRequestDTO.getOrigin();
		String destIATA = flightSearchRequestDTO.getDestination();
		LocalDateTime departureTime = flightSearchRequestDTO.getDepartureDate();
		LocalDateTime arrivalTime = flightSearchRequestDTO.getDepartureDate();
		BigDecimal maxPrice = flightSearchRequestDTO.getMaxPrice();

	 	List<Flight> filteredFlights =  flightRepository.findByOriginAndDestinationAndDepartureTimeAndArrivalTime(originIATA, destIATA, departureTime, arrivalTime);
	 	List<Long> flightIds = new ArrayList<>();
	 	Map<Long, Flight> flightIdToFlightMap = new HashMap<>();
	 	List<Long> airlineIds = new ArrayList<>();

	 	for (Flight flight : filteredFlights) {
	 		flightIds.add(flight.getId());
	 		flightIdToFlightMap.put(flight.getId(), flight);
	 		airlineIds.add(flight.getAirline().getId());
	 	}

	 	List<FlightPrice> flightPrices = flightPriceRepository.findAllByFlightIdAndByDepartureDateIn(flightIds, departureTime);

	 	Set<Long> flightClassIds = flightPrices.stream().map(fp -> fp.getFlightClass().getId()).collect(Collectors.toSet());
	 	List<FlightClass> relevantFlightClasses = flightClassRepository.findAllByIdIn(flightClassIds.stream().toList());
	 	Map<Long, FlightClass> flightClassIdToFlightClassMap = relevantFlightClasses.stream().collect(Collectors.toMap(FlightClass::getId, fc -> fc));

		List<Airline> airlines = airlineRepository.findAllByIdIn(airlineIds);
	 	Map<Long, Airline> airlineIdToAirlineMap = airlines.stream().collect(Collectors.toMap(Airline::getId, a -> a));

	 	// Construct FlightOffferDTO
	 	List<FlightOfferDTO> flightOfferDTOs = new ArrayList<>();

	 	for (FlightPrice thisFlightPrice : flightPrices) {
			 FlightOfferDTO foDTO = new FlightOfferDTO();

			 //Set FlightOffferDTO fields
			 Flight thisFlight = flightIdToFlightMap.get(thisFlightPrice.getFlight().getId());

			 foDTO.setOrigin(thisFlight.getOrigin());
			 foDTO.setDestination(thisFlight.getDestination());
			 foDTO.setDepartureDate(thisFlight.getDepartureTime());
			 foDTO.setArrivalDate(thisFlight.getArrivalTime());
			 foDTO.setFlightPriceId(thisFlightPrice.getId());
			 foDTO.setBasePrice(thisFlightPrice.getBasePrice());
			 foDTO.setFlightNumber(thisFlight.getFlightNumber());

			 //Set FlightClass Name + Id
			 foDTO.setFlightClassId(thisFlightPrice.getFlightClass().getId());
			 foDTO.setFlightClassName(flightClassIdToFlightClassMap.get(thisFlightPrice.getFlightClass().getId()).getClassName());

			 //Set Airline Name + Id
			 foDTO.setAirlineId(thisFlight.getAirline().getId());
			 foDTO.setAirlineName(airlineIdToAirlineMap.get(thisFlight.getAirline().getId()).getName());

			 flightOfferDTOs.add(foDTO);
	 	}

	 	return flightOfferDTOs;
	}
}



