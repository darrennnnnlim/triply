package com.example.triply.core.flight.repository;

import com.example.triply.core.flight.model.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

        List<Airline> findAllByIdIn(List<Long> airlineIds);
}
