package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {

    List<Bus> findBySourceIgnoreCaseAndDestinationIgnoreCase(
            String source,
            String destination);
}