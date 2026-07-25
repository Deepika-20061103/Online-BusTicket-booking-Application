package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {
	List<Ticket> findByPhoneNumber(String phoneNumber);
    List<Ticket> findByBookedBy(
            String bookedBy);
    @Query("SELECT t.seatNo FROM Ticket t WHERE t.busId = :busId AND t.journeyDate = :date")
    List<String> findBookedSeats(@Param("busId") Long busId,
                                 @Param("date") LocalDate date);

    List<Ticket> findByPassnameContainingIgnoreCase(
            String name);

    Ticket findByBusIdAndSeatNoAndJourneyDate(
            Long busId,
            String seatNo,
            LocalDate journeyDate);

    List<Ticket> findByBusId(
            Long busId);

    List<Ticket> findByBookedByAndJourneyDateBefore(
            String username,
            LocalDate date);

    List<Ticket> findByBookedByAndJourneyDateGreaterThanEqual(
            String username,
            LocalDate date);

    List<Ticket> findByBookedByAndStatus(
            String username,
            String status);
    List<Ticket> findByBookedByAndStatusAndJourneyDateGreaterThanEqualOrderByJourneyDateAsc(
            String bookedBy,
            String status,
            LocalDate date
    );
	long countByStatus(String status);
	boolean existsByBusIdAndSeatNoAndJourneyDate(Long busId, String seatNo, LocalDate journeyDate);
	long countByBusIdAndJourneyDateAndStatus(Long busId,
            LocalDate journeyDate,
            String status);
   

        
    }
    
