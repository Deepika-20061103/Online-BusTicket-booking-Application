package com.example.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusService {

    @Autowired
    private BusRepository repo;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    public Bus saveBus(Bus bus) {
        return repo.save(bus);
    }

    public List<Bus> getAllBuses() {
        return repo.findAll();
    }

    public Bus getBusById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void deleteBus(Long id) {
        repo.deleteById(id);
    }

    public Bus findById(Long busId) {
        return repo.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + busId));
    }

    /**
     * Every bus runs daily. If journeyDate is today, buses whose departure
     * time has already passed are excluded.
     */
    public List<Bus> search(String source, String destination, LocalDate journeyDate) {

        List<Bus> buses =
                repo.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);

        if (journeyDate.isEqual(LocalDate.now())) {

            LocalTime now = LocalTime.now();

            buses = buses.stream()
                    .filter(b -> isDepartureAfter(b.getDepartureTime(), now))
                    .collect(Collectors.toList());
        }

        return buses;
    }

    private boolean isDepartureAfter(String departureTimeStr, LocalTime now) {
        try {
            LocalTime departure = LocalTime.parse(departureTimeStr, TIME_FMT);
            return departure.isAfter(now);
        } catch (Exception e) {
            // if a time string is malformed, don't silently hide the bus
            return true;
        }
    }

    public void bookSeats(Long busId, int seats, user loggedUser) {

        Bus bus = repo.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        if (bus.getAvailableSeats() < seats) {
            throw new RuntimeException("Not enough seats available");
        }

        bus.setAvailableSeats(bus.getAvailableSeats() - seats);

        repo.save(bus);
    }
}