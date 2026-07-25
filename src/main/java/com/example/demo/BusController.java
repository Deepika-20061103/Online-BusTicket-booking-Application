package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BusController {

    @Autowired
    private BusService busService;
    @Autowired
    private TicketRepository ticketRepo;
    private BusController repo;

    // matches "08:00 AM" style strings stored on Bus.departureTime / arrivalTime
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * Combines a journey date with a bus's stored time-of-day string into a
     * real LocalDateTime. Returns null if the time string can't be parsed
     * (bad data shouldn't crash the page - treat as "unknown, not departed").
     */
    private LocalDateTime combine(LocalDate journeyDate, String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr.trim().toUpperCase(), TIME_FORMAT);
            return LocalDateTime.of(journeyDate, time);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    private boolean hasDeparted(LocalDate journeyDate, String departureTimeStr) {
        LocalDateTime departure = combine(journeyDate, departureTimeStr);
        return departure != null && departure.isBefore(LocalDateTime.now());
    }

    @GetMapping("/addbuss")
    public String addBusPage(Model model, HttpSession session) {
        user loggedUser = (user) session.getAttribute("user");
        if (loggedUser == null || !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("bus", new Bus());
        return "addbuss";
    }

    @PostMapping("/savebus")
    public String saveBus(@ModelAttribute Bus bus, HttpSession session) {

        System.out.println("Journey Date : " + bus.getJourneyDate());
        System.out.println("Departure    : " + bus.getDepartureTime());
        System.out.println("Arrival      : " + bus.getArrivalTime());

        user loggedUser = (user) session.getAttribute("user");
        if (loggedUser == null || !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }

        busService.saveBus(bus);

        return "redirect:/owner";
    }

    @GetMapping("/searchBus")
    public String searchBus(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) LocalDate journeyDate,
            Model model,
            HttpSession session) {

        LocalDate today = LocalDate.now();

        LocalDate date = (journeyDate != null && !journeyDate.isBefore(today))
                ? journeyDate
                : today;

        session.setAttribute("source", source);
        session.setAttribute("destination", destination);

        List<Bus> buses = busService.search(source, destination, date);

        // Use India time
        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalTime now = LocalTime.now(zone);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        for (Bus bus : buses) {

            // Future dates → always allow booking
            if (date.isAfter(today)) {
                bus.setBookingAllowed(true);
                continue;
            }

            // Today → allow only before departure
            try {

                LocalTime departure = LocalTime.parse(
                        bus.getDepartureTime().trim(),
                        formatter);

                bus.setBookingAllowed(now.isBefore(departure));

            } catch (Exception e) {

                System.out.println("Invalid departure time: " + bus.getDepartureTime());

                // If parsing fails, allow booking
                bus.setBookingAllowed(true);
            }
        }

        model.addAttribute("buses", buses);
        model.addAttribute("journeyDate", date);
        model.addAttribute("today", today);

        return "buslist";
    }
    @GetMapping("/buses")
    public String buses(@RequestParam(required = false) LocalDate journeyDate,
                        Model model,
                        HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");

        if (journeyDate == null) {
            journeyDate = LocalDate.now();
        }

        List<Bus> buses = busService.getAllBuses();

        for (Bus bus : buses) {

            long bookedSeats = ticketRepo.countByBusIdAndJourneyDateAndStatus(
                    bus.getBusId(),
                    journeyDate,
                    "BOOKED");

            bus.setBookedSeats((int) bookedSeats);

            bus.setAvailableSeats(40 - (int) bookedSeats);
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("journeyDate", journeyDate);
        model.addAttribute("buses", buses);

        return "buslist";
    }
    @GetMapping("/deleteBus/{id}")
    public String deleteBus(@PathVariable Long id,
                            HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");

        if (loggedUser == null ||
            !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }

        busService.deleteBus(id);

        return "redirect:/buses";
    }
    // ---- Booking flow ----

   

    @PostMapping("/confirmbooking")
    public String confirmBooking(@RequestParam Long busId,
                                  @RequestParam LocalDate journeyDate,
                                  @RequestParam int seats,
                                  HttpSession session,
                                  Model model) {
        user loggedUser = (user) session.getAttribute("user");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Bus bus = busService.getBusById(busId);
        if (bus == null) {
            model.addAttribute("error", "Bus not found.");
            return "redirect:/buses";
        }

        // re-check at confirmation time too - time may have passed between
        // page load and form submission
        if (journeyDate.isBefore(LocalDate.now())
                || hasDeparted(journeyDate, bus.getDepartureTime())) {
            model.addAttribute("error", "This bus has already departed and cannot be booked.");
            return "redirect:/buses";
        }

        if (seats > bus.getAvailableSeats()) {
            model.addAttribute("error", "Not enough seats available.");
            model.addAttribute("bus", bus);
            model.addAttribute("journeyDate", journeyDate);
            return "bookticket";
        }

        busService.bookSeats(busId, seats, loggedUser);
        return "redirect:/mybookings";
    }
    @GetMapping("/editBus/{id}")
    public String editBus(@PathVariable Long id,
                          Model model,
                          HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");

        if (loggedUser == null ||
            !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }

        Bus bus = busService.getBusById(id);

        model.addAttribute("bus", bus);

        return "editBus";
    }
    @PostMapping("/updateBus")
    public String updateBus(@ModelAttribute Bus bus,
                            HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");

        if (loggedUser == null ||
            !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }

        busService.saveBus(bus);

        return "redirect:/buses";
    }
    public void save(Bus bus) {
        repo.save(bus);
    }
    
}