package com.example.demo;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TicketController {

    @Autowired
    private TicketService service;
    @Autowired
    private BusService busService;
    @Autowired
    private TicketRepository repo;
    @Autowired
    private BusRepository busRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private OTPService otpService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private EmailService emailService;
    

    @PostMapping("/save")
    public String save(@ModelAttribute Ticket ticket, HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        // validations (keep yours as it is)

        ticket.setBookedBy(loggedUser.getUsername());
        ticket.setPassname(loggedUser.getUsername());
        ticket.setEmail(loggedUser.getEmail());
        ticket.setPhoneNumber(loggedUser.getPhno());
        ticket.setStatus("BOOKED");

        Bus bus = busService.getBusById(ticket.getBusId());
        ticket.setBusName(bus.getBusName());
        ticket.setSource(bus.getSource());
        ticket.setDest(bus.getDestination());
        ticket.setBookingDate(LocalDate.now());

        // ✅ OTP GENERATION
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        session.setAttribute("otp", otp);
        session.setAttribute("otpTime", java.time.LocalDateTime.now());
        session.setAttribute("pendingTicket", ticket);

        emailService.sendOtp(ticket.getEmail(), otp);

        return "otp";
    }
    @GetMapping("/tickets")
    public String viewTickets(Model model,
                              HttpSession session) {

        user loggedUser =
                (user) session.getAttribute("user");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        // OWNER can see all tickets
        if ("OWNER".equalsIgnoreCase(
                loggedUser.getRole())) {

            model.addAttribute(
                    "tickets",
                    service.fetchTickets());

        } else {

            // USER can see only their tickets
            model.addAttribute(
                    "tickets",
                    service.fetchTicketsByUser(
                            loggedUser.getUsername()));
        }

        return "tickets";
    }

  
    @GetMapping("/cancel/{id}")
    public String cancel(
            @PathVariable Long id,
            HttpSession session) {

        user loggedUser =
                (user) session.getAttribute("user");

        if(loggedUser == null){
            return "redirect:/login";
        }

        // Get ticket before cancelling
        Ticket ticket = repo.findById(id).orElse(null);

        if(ticket != null){

            // Increase available seats
            Bus bus = busService.getBusById(ticket.getBusId());

            bus.setAvailableSeats(bus.getAvailableSeats() + 1);

            busRepo.save(bus);
        }

        // Cancel ticket
        service.cancelTicket(id);

        return "redirect:/tickets";
    }

    @GetMapping("/owner")
    public String ownerDashboard(Model model,
                                 HttpSession session) {

        user loggedUser = (user) session.getAttribute("user");

        if (loggedUser == null ||
            !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {

            return "redirect:/login";
        }

        model.addAttribute("tickets", service.fetchTickets());

        model.addAttribute("totalUsers", userRepo.count());

        model.addAttribute("totalBuses", busRepo.count());

        model.addAttribute("totalTickets", repo.count());

        model.addAttribute("bookedCount",
                service.countByStatus("BOOKED"));

        model.addAttribute("cancelledCount",
                service.countByStatus("CANCELLED"));

        return "owner";
    }
    @GetMapping("/owner/cancel/{id}")
    public String ownerCancelTicket(
            @PathVariable Long id) {

        Ticket ticket = repo.findById(id).orElse(null);

        if(ticket != null){

            Bus bus = busService.getBusById(ticket.getBusId());

            bus.setAvailableSeats(bus.getAvailableSeats() + 1);

            busRepo.save(bus);
        }

        service.cancelTicket(id);

        return "redirect:/owner";
    }
    
    @GetMapping("/booking")
    public String booking(@RequestParam Long busId,
                          @RequestParam(required = false) LocalDate journeyDate,
                          Model model) {

        Bus bus = busService.findById(busId);

        LocalDate today = LocalDate.now();

        // default to today, and never allow a date before today
        LocalDate date = (journeyDate != null && !journeyDate.isBefore(today))
                ? journeyDate
                : today;

        Ticket ticket = new Ticket();
        ticket.setBusId(busId);
        ticket.setBusName(bus.getBusName());
        ticket.setSource(bus.getSource());
        ticket.setDest(bus.getDestination());
        ticket.setJourneyDate(date);   // <-- was missing, so the date field kept resetting on reload

        model.addAttribute("ticket", ticket);

        List<String> bookedSeats = service.getBookedSeats(busId, date);
        model.addAttribute("bookedSeats", bookedSeats);

        // for the HTML min= attribute and for showing "current date/time"
        model.addAttribute("today", today);
        model.addAttribute("now", java.time.LocalDateTime.now());

        return "index";
    }
    @GetMapping("/previousJourneys")
    public String previousJourneys(
            HttpSession session,
            Model model){

        user loggedUser =
                (user) session.getAttribute("user");

        if(loggedUser == null){
            return "redirect:/login";
        }

        model.addAttribute(
                "journeys",
                service.getPreviousJourneys(
                        loggedUser.getUsername()));

        return "previousJourneys";
    }
    @GetMapping("/searchPassenger")
    public String searchPassenger(
            @RequestParam String keyword,
            Model model,
            HttpSession session){

        user loggedUser =
                (user) session.getAttribute("user");

        if(loggedUser == null ||
           !"OWNER".equalsIgnoreCase(
                   loggedUser.getRole())){

            return "redirect:/login";
        }

        model.addAttribute(
                "tickets",
                repo.findByPassnameContainingIgnoreCase(
                        keyword));

        return "tickets";
    }
    @GetMapping("/upcomingTickets")
    public String upcomingTickets(
            HttpSession session,
            Model model){

        user loggedUser =
                (user) session.getAttribute("user");

        if(loggedUser == null){
            return "redirect:/login";
        }

        model.addAttribute(
                "tickets",
                service.getUpcomingTickets(
                        loggedUser.getUsername()));

        return "upcomingTickets";
    }
    @GetMapping("/cancelledTickets")
    public String cancelledTickets(
            HttpSession session,
            Model model){

        user loggedUser =
                (user) session.getAttribute("user");

        if(loggedUser == null){
            return "redirect:/login";
        }

        model.addAttribute(
                "tickets",
                service.getCancelledTickets(
                        loggedUser.getUsername()));

        return "cancelledTickets";
    }
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {

        user loggedUser = (user) session.getAttribute("user");

        if (loggedUser == null ||
            !"OWNER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalBuses", busService.getAllBuses().size());
        model.addAttribute("totalTickets", service.fetchTickets().size());

        model.addAttribute("bookedCount", service.countByStatus("BOOKED"));
        model.addAttribute("cancelledCount", service.countByStatus("CANCELLED"));

        model.addAttribute("totalRevenue", service.getTotalRevenue());

        return "reports";
    }
    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam String otp,
                            HttpSession session) {

        String savedOtp = (String) session.getAttribute("otp");
        java.time.LocalDateTime otpTime =
                (java.time.LocalDateTime) session.getAttribute("otpTime");

        if (otpTime == null ||
            otpTime.plusMinutes(5).isBefore(java.time.LocalDateTime.now())) {

            session.removeAttribute("otp");
            session.removeAttribute("otpTime");
            session.removeAttribute("pendingTicket");

            return "redirect:/otp?expired";
        }

        if (savedOtp == null || !otp.equals(savedOtp)) {
            return "redirect:/otp?error";
        }

        Ticket ticket = (Ticket) session.getAttribute("pendingTicket");

     // Save ticket
     repo.save(ticket);

     // Reduce available seats
     Bus bus = busService.getBusById(ticket.getBusId());

     bus.setAvailableSeats(bus.getAvailableSeats() - 1);

     busRepo.save(bus);

     // Send ticket email
     emailService.sendTicket(ticket);

        session.removeAttribute("otp");
        session.removeAttribute("otpTime");
        session.removeAttribute("pendingTicket");

        return "confirmation";
    }
    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session) {

        Ticket ticket = (Ticket) session.getAttribute("pendingTicket");

        if (ticket == null) {
            return "redirect:/";
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        session.setAttribute("otp", otp);
        session.setAttribute("otpTime", java.time.LocalDateTime.now());

        emailService.sendOtp(ticket.getEmail(), otp);

        return "redirect:/otp?resent";
    }
    @GetMapping("/otp")
    public String otpPage(HttpSession session) {

        Ticket ticket = (Ticket) session.getAttribute("pendingTicket");

        if (ticket == null) {
            return "redirect:/";
        }

        return "otp";
    }
   

    
    
}