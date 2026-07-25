package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SeatController {

    @Autowired
    private BusService busService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/seat/{id}")
    public String seatPage(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "bus",
                busService.getBusById(id));

        model.addAttribute(
                "bookedSeats",
                ticketService.getBookedSeats(id));

        return "seat";
    }
}