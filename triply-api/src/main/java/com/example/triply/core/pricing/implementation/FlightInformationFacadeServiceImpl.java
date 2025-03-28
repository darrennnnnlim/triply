public class FlightInformationFacadeServiceImpl {

	public List<FlightSearchDTO> getFlightPricesByCriteria(CriteriaDTO criteriaDTO) {
		//TODO:  Map origin/dest name to IATA airport code
	 	List<Flight> filteredFlights =  FlightRepository.findByOriginAndDestinationAndDepartureTimeAndArrivalTime(originIATA, destIATA, departureTime, arrivalTime);
	 	List<Long> flightIds = new ArrayList<>();
	 	Map<Long, Flight> flightIdToFlightMap = new HashMap<>();
	 	List<Long> airlineIds = new ArrayList<>();

	 	for (Flight flight : filteredFlights) {
	 		flightIds.add(flight.getId());
	 		flightIdToFlightMap.put(flight.getId(), flight);
	 		airlineIds.add(flight.getAirlineId());
	 	}
	 	
	 	//TODO: Edit search method to ensure only day matches for departuredate search in repo method
	 	List<FlightPrice> flightPrices = FlightPriceRepository.findAllInFlightIdAndByDepartureDate(flightIds, departureTime.getDate());

	 	Set<Long> flightClassIds = flightPrices.stream().collect(Collectors.groupingBy(FlightPrice::getFlightClassId));
	 	List<FlightClass> relevantFlightClasses = FlightClassRepository.findAllInId(flightClassIds);
	 	Map<Long, FlightClass> flightClassIdToFlightClassMap = relevantFlightClasses.stream().collect(Collectors.toMap(FlightClass::getId, fc -> fc));

	 	List<Airline> airlines = AirlineRepository.findAllIn(airlineIds);
	 	Map<Long, Airline> airlineIdToAirlineMap = airlines.stream().collect(Collectors.toMap(Airline::getId, a -> a));

	 	// Construct FlightSearchDTO
	 	List<FlightSearchDTO> flightSearchDTOs = new ArrayList<>();

	 	for (FlightPrice thisFlightPrice : flightPrices) {
	 		FlightSearchDTO fsDTO = new FlightSearchDTO();
	 		fsDTO.setFlightNumber(thisFlightPrice)

	 		//TODO: Set everything for Flight
	 		Flight thisFlight = flightIdToFlightMap.get(thisFlightPrice.getFlightId());

	 		//Set FlightClass Name + Id
	 		fsDTO.setFlightClassId(thisFlightPrice.getFlightClassId());
	 		fsDTO.setFlightClassName(flightClassIdToFlightClassMap.get(thisFlightPrice.getFlightClassId()));

	 		//Set Airline Name + Id
	 		fsDTO.setAirlineId(thisFlight.getAirlineId);
	 		fsDTO.setAirlineName(airlineIdToAirlineMap.get(thisFlight.getAirlineId).getName());
	 		
	 		flightSearchDTOs.add(fsDTO);
	 	}

	 	return flightSearchDTOs;
	}
}


public interface FlightRepository {
	List<Flight> findByOriginAndDestinationAndDepartureTimeAndArrivalTime(String originIATA, String destIATA, LocalDateTime departureTime, LocalDateTime arrivalTime);
}

public interface FlightPriceRepository {

	//TODO: Same day matching, but not necessarily same time
	List<FlightPrice> findAllInFlightIdAndByDepartureDate(List<Long> flightIds, LocalDateTime DepartureDate);
}

public interface FlightClassRepository {
	List<FlightClass> findAllInId(flightClassIds);
}


public interface FlightClassRepository {
	List<Airline> findAllIn(airlineIds);
}

public class HelperRepoMethod {
	
	// Potential cool helper method
	public Map<Long, T> genericMapGenerateFromList(List<T> list) {
		return list.stream().collect(Collectors.toMap(T::getId, item -> item));
	}
}

