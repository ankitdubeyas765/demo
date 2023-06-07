package com.skyscanner.repository;



import com.skyscanner.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByLeavingFromAndGoingToAndDepartureDateAndArrivalDate(
            String leavingFrom, String goingTo, LocalDate departureDate, LocalDate arrivalDate);
    Page<Trip> findAll(Pageable pageable);
}