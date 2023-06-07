package com.skyscanner.controller;


import com.skyscanner.entity.Passenger;
import com.skyscanner.entity.Trip;
import com.skyscanner.payload.PassengerDto;
import com.skyscanner.payload.TripDto;
import com.skyscanner.service.impl.TripService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    private final ModelMapper modelMapper;

    public TripController(TripService tripService, ModelMapper modelMapper) {
        this.tripService = tripService;
        this.modelMapper = modelMapper;
    }

    //http://localhost:8080/trips/1/download/excel
    //http://localhost:8080/trips
    @PostMapping
    public ResponseEntity<TripDto> addTrip(@RequestBody TripDto tripDto) {
        TripDto dto = tripService.addTrip(tripDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

   // http://localhost:8080/trips/search?leavingFrom={leavingFrom}&goingTo={goingTo}&departureDate={departureDate}&arrivalDate={arrivalDate}
    @GetMapping("/search")
    public ResponseEntity<List<Trip>> searchTrips(
            @RequestParam("leavingFrom") String leavingFrom,
            @RequestParam("goingTo") String goingTo,
            @RequestParam("departureDate") String departureDate,
            @RequestParam("arrivalDate") String arrivalDate
    ) {
        LocalDate depDate = LocalDate.parse(departureDate.trim());
        LocalDate arrDate = LocalDate.parse(arrivalDate.trim());

        List<Trip> trips = tripService.searchTrips(leavingFrom, goingTo, depDate, arrDate);

        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(trips);
    }

    @GetMapping("/page")
    public Page<TripDto> getAllTrips(Pageable pageable) {
        return tripService.getAllTrips(pageable);
    }
   // http://localhost:8080/trips/{tripId}/passengers

    @PostMapping("/{tripId}/passengers")
    public ResponseEntity<TripDto> addPassengerToTrip(
            @PathVariable("tripId") Long tripId,
            @RequestBody PassengerDto passengerDto
    ) {
        // Fetch the trip by ID
        Trip trip = tripService.getTripById(tripId);

        if (trip == null) {
            return ResponseEntity.notFound().build();
        }

        // Create a Passenger entity from the DTO
        Passenger passenger = new Passenger();
        passenger.setName(passengerDto.getName());
        passenger.setAge(passengerDto.getAge());
        passenger.setEmail(passengerDto.getEmail());
        passenger.setMobile(passengerDto.getMobile());
        passenger.setTrip(trip);

        // Add the passenger to the trip
        trip.getPassengers().add(passenger);

        // Save the updated trip
        Trip savedTrip = tripService.saveTrip(trip);

        return ResponseEntity.ok(modelMapper.map(savedTrip, TripDto.class));
    }
   // http://localhost:8080/trips/{tripId}/download/excel

    @GetMapping("/{tripId}/download/excel")
    public void downloadTripRecordsAsExcel(
            @PathVariable("tripId") Long tripId,
            HttpServletResponse response
    ) throws IOException {
        Trip trip = tripService.getTripById(tripId);

        if (trip == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trip not found");
            return;
        }

        // Generate the Excel file
        byte[] excelBytes = generateExcel(trip);

        // Set the response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentLength(excelBytes.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"trip_records.xlsx\"");

        // Write the Excel bytes to the response output stream
        response.getOutputStream().write(excelBytes);
    }

    private byte[] generateExcel(Trip trip) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Trip Records");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Leaving From");
        headerRow.createCell(2).setCellValue("Going To");
        headerRow.createCell(3).setCellValue("Departure Date");
        headerRow.createCell(4).setCellValue("Arrival Date");
        headerRow.createCell(5).setCellValue("Passenger Name");
        headerRow.createCell(6).setCellValue("Passenger Age");
        headerRow.createCell(7).setCellValue("Passenger Email");
        headerRow.createCell(8).setCellValue("Passenger Mobile");

        // Create data row for trip details
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(trip.getId());
        dataRow.createCell(1).setCellValue(trip.getLeavingFrom());
        dataRow.createCell(2).setCellValue(trip.getGoingTo());
        dataRow.createCell(3).setCellValue(trip.getDepartureDate().toString());
        dataRow.createCell(4).setCellValue(trip.getArrivalDate().toString());

        // Create data rows for passenger details
        int rowIndex = 2; // Start from row index 2 for passenger data
        for (Passenger passenger : trip.getPassengers()) {
            Row passengerDataRow = sheet.createRow(rowIndex++);
            passengerDataRow.createCell(5).setCellValue(passenger.getName());
            passengerDataRow.createCell(6).setCellValue(passenger.getAge());
            passengerDataRow.createCell(7).setCellValue(passenger.getEmail());
            passengerDataRow.createCell(8).setCellValue(passenger.getMobile());
        }

        // Auto-size columns for better visibility
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the workbook to a byte array
        byte[] excelBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            excelBytes = outputStream.toByteArray();
        }

        workbook.close();

        return excelBytes;
    }


}


