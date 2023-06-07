package com.skyscanner.payload;



import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class TripDto {
    private Long id;
    private String leavingFrom;
    private String goingTo;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private int travellers;
    private String type;
    private int seats;
    private double price;
    private List<PassengerDto> passengers;



}
