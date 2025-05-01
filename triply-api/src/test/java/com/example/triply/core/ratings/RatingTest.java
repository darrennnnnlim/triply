package com.example.triply.core.ratings;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.entity.Ratings;
import com.example.triply.core.ratings.repository.RatingRepository;
import com.example.triply.core.ratings.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    private UserRepository userRepository;
    private FlightBookingRepository flightBookingRepository;
    private HotelBookingRepository hotelBookingRepository;
    private RatingRepository ratingRepository;
    private RatingService ratingService;
    private FlightRepository flightRepository;

    private User user;
    private User user2;
    private Flight flight;
    private Airline airline;
    private FlightBooking flightBooking;
    private HotelBooking hotelBooking;
    private RatingRequest ratingRequest;
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        flightBookingRepository = mock(FlightBookingRepository.class);
        hotelBookingRepository = mock(HotelBookingRepository.class);
        ratingRepository = mock(RatingRepository.class);
        flightRepository = mock(FlightRepository.class);

        ratingService = new RatingService(userRepository, flightBookingRepository, hotelBookingRepository, ratingRepository, flightRepository);


        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");

        airline = new Airline();
        airline.setId(1L);
        flight = new Flight();
        flight.setId(1L);
        flight.setAirline(airline);

        flightBooking = new FlightBooking();
        flightBooking.setId(1L);
        flightBooking.setFlight(flight);


        hotelBooking = new HotelBooking();
        hotelBooking.setId(1L);

        ratingRequest = new RatingRequest();
    }

    @Test
    void testSaveFlightRating() {
        ratingRequest.setType("Flight");
        ratingRequest.setFlightId(1L);
        ratingRequest.setUserId(1L);
        ratingRequest.setDelete("F");
        ratingRequest.setRating(5);
        ratingRequest.setHotelId(null);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(1L)).thenReturn(java.util.Optional.of(flightBooking));
        when(ratingRepository.findByUserAndFlightBooking(user, flightBooking)).thenReturn(null);


        Ratings savedRating = new Ratings();
        savedRating.setId(1L);
        when(ratingRepository.save(any(Ratings.class))).thenReturn(savedRating);


        RatingResponse response = ratingService.saveRating(ratingRequest);

        assertNotNull(response);
        assertEquals(1L, response.getFlightId());
        assertNull(response.getHotelId());
        assertEquals(1L, response.getId());
    }

    @Test
    void testSaveHotelRating() {
        ratingRequest.setType("Hotel");
        ratingRequest.setHotelId(1L);
        ratingRequest.setUserId(1L);
        ratingRequest.setDelete("F");
        ratingRequest.setRating(3);
        ratingRequest.setFlightId(null);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(hotelBookingRepository.findById(1L)).thenReturn(java.util.Optional.of(hotelBooking));
        when(ratingRepository.findByUserAndHotelBooking(user, hotelBooking)).thenReturn(null);


        Ratings savedRating = new Ratings();
        savedRating.setId(2L);
        when(ratingRepository.save(any(Ratings.class))).thenReturn(savedRating);

        RatingResponse response = ratingService.saveRating(ratingRequest);


        assertNotNull(response);
        assertEquals(1L, response.getHotelId());
        assertNull(response.getFlightId());
        assertEquals(2L, response.getId());
    }

    @Test
    void testUpdateFlightRating() {
        ratingRequest.setType("Flight");
        ratingRequest.setHotelId(null);
        ratingRequest.setUserId(1L);
        ratingRequest.setDelete("F");
        ratingRequest.setRating(5);
        ratingRequest.setFlightId(1L);


        Ratings existingRating = new Ratings();
        existingRating.setRating(3);
        existingRating.setUser(user);
        existingRating.setFlightBooking(flightBooking);


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(1L)).thenReturn(java.util.Optional.of(flightBooking));
        when(ratingRepository.findByUserAndFlightBooking(user, flightBooking)).thenReturn(existingRating);


        Ratings savedRating = new Ratings();
        savedRating.setId(1L);
        savedRating.setUser(user);
        savedRating.setFlightBooking(flightBooking);

        when(ratingRepository.save(any(Ratings.class))).thenReturn(savedRating);


        RatingResponse response = ratingService.saveRating(ratingRequest);


        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals(1L, response.getFlightId());
        assertNull(response.getHotelId());
        assertEquals(1L, response.getId());
    }

    @Test
    void testUserNotFound() {
        ratingRequest.setType("Flight");
        ratingRequest.setFlightId(1L);
        ratingRequest.setUserId(1L);


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty()); // User not found


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.saveRating(ratingRequest);
        });


        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testFlightBookingNotFound() {

        ratingRequest.setType("Flight");
        ratingRequest.setFlightId(1L);
        ratingRequest.setUserId(1L);


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(1L)).thenReturn(java.util.Optional.empty()); // Flight booking not found


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.saveRating(ratingRequest);
        });


        assertEquals("Flight Booking not found", exception.getMessage());
    }

    @Test
    void testHotelBookingNotFound() {

        ratingRequest.setType("Hotel");
        ratingRequest.setHotelId(1L);
        ratingRequest.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(hotelBookingRepository.findById(1L)).thenReturn(java.util.Optional.empty()); // Hotel booking not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.saveRating(ratingRequest);
        });

        assertEquals("Hotel Booking not found", exception.getMessage());
    }

    @Test
    void testGetRatingsByUserId() {

        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setUser(user);
        rating1.setDelete("F");
        rating1.setFlightBooking(flightBooking);
        rating1.setHotelBooking(null);

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setRating(4);
        rating2.setUser(user);
        rating2.setDelete("F");
        rating2.setFlightBooking(null);
        rating2.setHotelBooking(hotelBooking);


        when(ratingRepository.findByUserId(1L)).thenReturn(List.of(rating1, rating2));


        List<RatingResponse> response = ratingService.getRatingsByUserId(1L);


        assertNotNull(response);
        assertEquals(2, response.size());


        RatingResponse resp1 = response.get(0);
        assertEquals(1L, resp1.getId());
        assertEquals(5, resp1.getRating());
        assertEquals(1L, resp1.getUserId());
        assertEquals("F", resp1.getDelete());
        assertEquals(1L, resp1.getFlightId());
        assertNull(resp1.getHotelId());


        RatingResponse resp2 = response.get(1);
        assertEquals(2L, resp2.getId());
        assertEquals(4, resp2.getRating());
        assertEquals(1L, resp2.getUserId());
        assertEquals("F", resp2.getDelete());
        assertNull(resp2.getFlightId());
        assertEquals(1L, resp2.getHotelId());
    }


    @Test
    void testGetAllRatings() {
        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setUser(user);
        rating1.setDelete("F");
        rating1.setFlightBooking(flightBooking);
        rating1.setHotelBooking(null);

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setRating(4);
        rating2.setUser(user);
        rating2.setDelete("F");
        rating2.setFlightBooking(null);
        rating2.setHotelBooking(hotelBooking);

        Ratings rating3 = new Ratings();
        rating3.setId(3L);
        rating3.setRating(4);
        rating3.setUser(user2);
        rating3.setDelete("F");
        rating3.setFlightBooking(null);
        rating3.setHotelBooking(hotelBooking);


        when(ratingRepository.findAll()).thenReturn(List.of(rating1, rating2, rating3));


        List<RatingResponse> response = ratingService.getAllRatings();


        assertNotNull(response);
        assertEquals(3, response.size());


        RatingResponse resp1 = response.get(0);
        assertEquals(1L, resp1.getId());
        assertEquals(5, resp1.getRating());
        assertEquals(1L, resp1.getUserId());
        assertEquals("F", resp1.getDelete());
        assertEquals(1L, resp1.getFlightId());
        assertNull(resp1.getHotelId());


        RatingResponse resp2 = response.get(1);
        assertEquals(2L, resp2.getId());
        assertEquals(4, resp2.getRating());
        assertEquals(1L, resp2.getUserId());
        assertEquals("F", resp2.getDelete());
        assertNull(resp2.getFlightId());
        assertEquals(1L, resp2.getHotelId());

        RatingResponse resp3 = response.get(2);
        assertEquals(3L, resp3.getId());
        assertEquals(4, resp3.getRating());
        assertEquals(1L, resp3.getUserId());
        assertEquals("F", resp3.getDelete());
        assertNull(resp3.getFlightId());
        assertEquals(1L, resp3.getHotelId());
    }

    @Test
    void testGetRatingsByHotelId() {
        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setUser(user);
        rating1.setDelete("F");
        rating1.setFlightBooking(flightBooking);
        rating1.setHotelBooking(null);

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setRating(4);
        rating2.setUser(user);
        rating2.setDelete("F");
        rating2.setFlightBooking(null);
        rating2.setHotelBooking(hotelBooking);



        when(ratingRepository.findByHotelId(1L)).thenReturn(List.of(rating2));

        List<RatingResponse> response = ratingService.getRatingsByHotelId(1L);

        assertNotNull(response);
        assertEquals(1, response.size());

        RatingResponse resp1 = response.get(0);
        assertEquals(2L, resp1.getId());
        assertEquals(4, resp1.getRating());
        assertEquals(1L, resp1.getUserId());
        assertEquals("F", resp1.getDelete());
        assertEquals(1L, resp1.getHotelId());


    }


    @Test
    void testGetRatingsByFlightId() {
        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setUser(user);
        rating1.setDelete("F");
        rating1.setFlightBooking(flightBooking);
        rating1.setHotelBooking(null);

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setRating(4);
        rating2.setUser(user);
        rating2.setDelete("F");
        rating2.setFlightBooking(null);
        rating2.setHotelBooking(hotelBooking);

        Ratings rating3 = new Ratings();
        rating3.setId(3L);
        rating3.setRating(4);
        rating3.setUser(user2);
        rating3.setDelete("F");
        rating3.setFlightBooking(null);
        rating3.setHotelBooking(hotelBooking);

        when(ratingRepository.findByFlightId(1L)).thenReturn(List.of(rating1));

        List<RatingResponse> response = ratingService.getRatingsByFlightId(1L);

        assertNotNull(response);
        assertEquals(1, response.size());

        RatingResponse resp1 = response.get(0);
        assertEquals(1L, resp1.getId());
        assertEquals(5, resp1.getRating());
        assertEquals(1L, resp1.getUserId());
        assertEquals("F", resp1.getDelete());
        assertEquals(1L, resp1.getFlightId());
        assertNull(resp1.getHotelId());

    }
    @Test
    void testSoftDeleteForFlight() {

        Ratings rating = new Ratings();
        rating.setDelete("F");


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(1L)).thenReturn(java.util.Optional.of(flightBooking));
        when(ratingRepository.findByUserAndFlightBooking(user, flightBooking)).thenReturn(rating);

        ratingService.softDelete(1L, 1L, null);

        verify(ratingRepository).save(rating);
        assertEquals("T", rating.getDelete());
    }

    @Test
    void testSoftDeleteForHotel() {

        Ratings rating = new Ratings();
        rating.setDelete("F");


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(hotelBookingRepository.findById(1L)).thenReturn(java.util.Optional.of(hotelBooking));
        when(ratingRepository.findByUserAndHotelBooking(user, hotelBooking)).thenReturn(rating);


        ratingService.softDelete(1L, null, 1L);


        verify(ratingRepository).save(rating);
        assertEquals("T", rating.getDelete());
    }

    @Test
    void testSoftDeleteNotFoundForFlight() {

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.softDelete(1L, 2L, null);
        });

        assertEquals("FlightBooking not found", exception.getMessage());
    }

    @Test
    void testSoftDeleteNotFoundForHotel() {

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(hotelBookingRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.softDelete(1L, null, 2L);
        });

        assertEquals("HotelBooking not found", exception.getMessage());
    }

    @Test
    void testSoftDeleteRatingsNotFound() {


        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(flightBookingRepository.findById(3L)).thenReturn(java.util.Optional.of(flightBooking));
        when(ratingRepository.findByUserAndFlightBooking(user, flightBooking)).thenReturn(null); // No rating found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.softDelete(1L, 3L, null);
        });

        assertEquals("Ratings not found for the provided criteria", exception.getMessage());
    }

    @Test
    void testGetRatingsByAirlineId() {
        Airline airline = new Airline();
        airline.setId(1L);

        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAirline(airline);

        FlightBooking flightBooking = new FlightBooking();
        flightBooking.setId(1L);
        flightBooking.setFlight(flight);

        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setRating(5);
        rating1.setUser(user);
        rating1.setDelete("F");
        rating1.setFlightBooking(flightBooking);
        rating1.setHotelBooking(null);

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setRating(4);
        rating2.setUser(user);
        rating2.setDelete("F");
        rating2.setFlightBooking(null);
        rating2.setHotelBooking(hotelBooking);

        List<Flight> flights = List.of(flight);
        List<FlightBooking> flightBookings = List.of(flightBooking);
        when(flightRepository.findAllByAirlineId(1L)).thenReturn(flights);
        when(flightBookingRepository.findAllByFlightIdIn(List.of(1L))).thenReturn(flightBookings);
        when(ratingRepository.findAllByFlightBookingIdIn(List.of(1L))).thenReturn(List.of(rating1));

        List<RatingResponse> response = ratingService.getRatingsByAirlineId(1L);


        assertNotNull(response);
        assertEquals(1, response.size());

        RatingResponse resp1 = response.get(0);
        assertEquals(1L, resp1.getId());
        assertEquals(5, resp1.getRating());
        assertEquals(1L, resp1.getUserId());
        assertEquals("F", resp1.getDelete());
        assertEquals(1L, resp1.getFlightId());
        assertNull(resp1.getHotelId());
    }

    @Test
    void testSoftDeleteAllBy() {

        user.setId(1L);

        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setDelete("F");

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setDelete("F");


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.findByUserId(1L)).thenReturn(List.of(rating1, rating2));


        ratingService.softDeleteAllBy(1L);


        verify(ratingRepository, times(1)).saveAll(List.of(rating1, rating2));  // Ensure saveAll is called once with the modified ratings


        assertEquals("T", rating1.getDelete());
        assertEquals("T", rating2.getDelete());
    }

    @Test
    void testUndoSoftDeleteAllBy() {

        user.setId(1L);

        Ratings rating1 = new Ratings();
        rating1.setId(1L);
        rating1.setDelete("T");

        Ratings rating2 = new Ratings();
        rating2.setId(2L);
        rating2.setDelete("T");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.findByUserId(1L)).thenReturn(List.of(rating1, rating2));


        ratingService.undoSoftDeleteAllBy(1L);


        verify(ratingRepository, times(1)).saveAll(List.of(rating1, rating2));  // Ensure saveAll is called once with the modified ratings


        assertEquals("F", rating1.getDelete());
        assertEquals("F", rating2.getDelete());
    }


}
