package com.skyscanner.service.impl;



import com.skyscanner.entity.Passenger;
import com.skyscanner.entity.Trip;
import com.skyscanner.payload.TripDto;
import com.skyscanner.repository.TripRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TripService(TripRepository tripRepository, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.modelMapper = modelMapper;
    }

    public TripDto addTrip(TripDto tripDto) {
        Trip trip = modelMapper.map(tripDto, Trip.class);

        // Map passenger details to Passenger entities and set the trip relationship
        List<Passenger> passengers = tripDto.getPassengers().stream()
                .map(passengerDto -> {
                    Passenger passenger = modelMapper.map(passengerDto, Passenger.class);
                    passenger.setTrip(trip);
                    return passenger;
                })
                .collect(Collectors.toList());

        trip.setPassengers(passengers);

        Trip savedTrip = tripRepository.save(trip);
        return modelMapper.map(savedTrip, TripDto.class);
    }
    public Page<TripDto> getAllTrips(Pageable pageable) {
        Page<Trip> tripPage = tripRepository.findAll(pageable);
        return tripPage.map(trip -> modelMapper.map(trip, TripDto.class));
    }
    public List<Trip> searchTrips(String leavingFrom, String goingTo, LocalDate departureDate, LocalDate arrivalDate) {
        return tripRepository.findByLeavingFromAndGoingToAndDepartureDateAndArrivalDate(
                leavingFrom, goingTo, departureDate, arrivalDate);
    }
    public Trip getTripById(Long tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        return trip.orElse(null);
    }

    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }
}
