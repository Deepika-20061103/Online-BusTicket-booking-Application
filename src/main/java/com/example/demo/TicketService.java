package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

	
    @Autowired
    private TicketRepository rep;

    public Ticket createTicket(Ticket ticket) {

        boolean exists = rep.existsByBusIdAndSeatNoAndJourneyDate(
                ticket.getBusId(),
                ticket.getSeatNo(),
                ticket.getJourneyDate()
        );

        if (exists) {
            throw new RuntimeException("Seat already booked for this date");
        }

        return rep.save(ticket);
    }

    public List<Ticket> fetchTickets() {
        return rep.findAll();
    }

    public List<Ticket> fetchTicketsByUser(
            String username) {

        return rep.findByBookedBy(username);
    }

    public void cancelTicket(Long id) {

        Ticket ticket =
                rep.findById(id).orElse(null);

        if(ticket != null) {
            ticket.setStatus("CANCELLED");
            rep.save(ticket);
        }
    }

    public long count() {
        return rep.count();
    }

    public long countByStatus(String status) {
        return rep.countByStatus(status);
    }

    public Ticket findByBusIdAndSeatNoAndJourneyDate(
            Long busId,
            String seatNo,
            LocalDate journeyDate) {

        return rep.findByBusIdAndSeatNoAndJourneyDate(
                busId,
                seatNo,
                journeyDate);
    }

    public List<Ticket> getBookedSeats(
            Long busId) {

        return rep.findByBusId(busId);
    }
    public List<Ticket> getPreviousJourneys(
            String username){

        return rep.findByBookedByAndJourneyDateBefore(
                username,
                LocalDate.now());
    }
    public List<Ticket> getUpcomingTickets(
            String username){

    	 return rep.findByBookedByAndStatusAndJourneyDateGreaterThanEqualOrderByJourneyDateAsc(
                 username,
                 "BOOKED",
                 LocalDate.now());
    }
    public List<Ticket> getCancelledTickets(
            String username){

        return rep.findByBookedByAndStatus(
                username,
                "CANCELLED");
    }
    public List<Ticket> findByPhoneNumber(String phoneNumber) {
        return rep.findByPhoneNumber(phoneNumber);
    }
    public double getTotalRevenue() {

        double revenue = 0;

        for (Ticket t : rep.findAll()) {

            if ("BOOKED".equalsIgnoreCase(t.getStatus())
                    && t.getFare() != null) {

                revenue += t.getFare();
            }
        }

        return revenue;
    }
    public boolean isSeatAlreadyBooked(Long busId, String seatNo, LocalDate journeyDate) {

        Ticket existing = rep.findByBusIdAndSeatNoAndJourneyDate(
                busId,
                seatNo,
                journeyDate
        );

        return existing != null;
    }
    public List<String> getBookedSeats(Long busId, LocalDate date) {
        return rep.findBookedSeats(busId, date);
    }
    
}