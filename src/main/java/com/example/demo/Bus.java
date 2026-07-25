package com.example.demo;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long busId;

    private String busName;
    private String source;
    private String destination;
    private String departureTime;   // format: "hh:mm a" e.g. "08:00 AM"
    private String arrivalTime;
    private Double fare;
    private int availableSeats=40;
    private LocalDate journeyDate;
  

    @Transient
    private int bookedSeats;

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Bus() {
        super();
    }
    

    public Bus(Long busId, String busName, String source, String destination,
                String departureTime, String arrivalTime, double fare, int availableSeats) {
        super();
        this.busId = busId;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.availableSeats = availableSeats;
    }
    @Transient
    private boolean bookingAllowed = true;

    public boolean isBookingAllowed() {
        return bookingAllowed;
    }

    public void setBookingAllowed(boolean bookingAllowed) {
        this.bookingAllowed = bookingAllowed;
    }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    
}