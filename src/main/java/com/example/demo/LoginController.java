package com.example.demo;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {


@Autowired
private UserRepository userRepo;
@Autowired
private TicketService ticketService;
@Autowired
private EmailService emailService;

@Autowired
private OTPService otpService;
@Autowired
private BusService busService;
// Home Page
@GetMapping("/")
public String home() {
    return "home";
}

// Login Page
@GetMapping("/login")
public String loginPage() {
    return "login";
}

// Login Process
@PostMapping("/login")
public String login(@RequestParam String username,
                    @RequestParam String password,
                    HttpSession session) {

    user existingUser = userRepo.findByUsername(username);

    if (existingUser == null) {
        return "redirect:/login?usernotfound";
    }

    if (!existingUser.getPassword().equals(password)) {
        return "redirect:/login?wrongpassword";
    }

    session.setAttribute("user", existingUser);

    if ("OWNER".equalsIgnoreCase(existingUser.getRole())) {
        return "redirect:/owner";
    }

    Long busId = (Long) session.getAttribute("selectedBusId");
    LocalDate journeyDate = (LocalDate) session.getAttribute("selectedJourneyDate");

    if (busId != null) {

        session.removeAttribute("selectedBusId");
        session.removeAttribute("selectedJourneyDate");

        return "redirect:/booking?busId=" + busId +
               "&journeyDate=" + journeyDate;
    }

    return "redirect:/dashboard";
}
// Book Ticket Access Check
@GetMapping("/bookticket")
public String bookTicket(@RequestParam(required = false) Long busId,
                         @RequestParam(required = false) LocalDate journeyDate,
                         HttpSession session) {

    if (busId == null) {
        return "redirect:/";
    }

    user loggedUser = (user) session.getAttribute("user");

    // If user is not logged in
    if (loggedUser == null) {

        // Save bus details in session
        session.setAttribute("selectedBusId", busId);
        session.setAttribute("selectedJourneyDate", journeyDate);

        return "redirect:/auth";
    }

    // User is logged in
    return "redirect:/booking?busId=" + busId +
           "&journeyDate=" + journeyDate;
}
@GetMapping("/auth")
public String authPage() {
    return "auth";
}
// Signup Page
@GetMapping("/signup")
public String signupPage() {
    return "signup";
}

// Register User
@PostMapping("/register")
public String register(
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam String phno,
        @RequestParam String password) {

    System.out.println("Username entered: " + username);

    user existingUser = userRepo.findByUsername(username);

    System.out.println("Existing user: " + existingUser);

    if (existingUser != null) {
        return "redirect:/signup?exists";
    }

    user newUser = new user();
    newUser.setUsername(username);
    newUser.setEmail(email);
    newUser.setPhno(phno);
    newUser.setPassword(password);
    newUser.setRole("USER");
    newUser.setCreatedDate(LocalDate.now());

    userRepo.save(newUser);

    return "redirect:/login";
}
// Logout
@GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
}

@GetMapping("/users")
public String viewUsers(HttpSession session,
                        Model model) {

    user loggedUser =
            (user) session.getAttribute("user");

    if (loggedUser == null ||
        !"OWNER".equalsIgnoreCase(
                loggedUser.getRole())) {

        return "redirect:/login";
    }

    model.addAttribute(
            "users",
            userRepo.findAll());

    return "users";
}
@GetMapping("/delete-user/{id}")
public String deleteUser(
        @PathVariable Long id,
        HttpSession session) {

    user loggedUser =
            (user) session.getAttribute("user");

    if(loggedUser == null ||
       !"OWNER".equalsIgnoreCase(
               loggedUser.getRole())) {

        return "redirect:/login";
    }

    userRepo.deleteById(id);

    return "redirect:/users";
}
@GetMapping("/dashboard")
public String userDashboard(HttpSession session,
                            Model model) {

    user loggedUser = (user) session.getAttribute("user");

    if (loggedUser == null) {
        return "redirect:/login";
    }

    model.addAttribute("user", loggedUser);

    // Show tickets booked with the logged-in user's phone number
    model.addAttribute(
            "tickets",
            ticketService.findByPhoneNumber(loggedUser.getPhno()));

    return "dashboard";
}

@GetMapping("/profile")
public String profile(
        HttpSession session,
        Model model){

    user loggedUser =
            (user) session.getAttribute("user");

    if(loggedUser == null){
        return "redirect:/login";
    }

    model.addAttribute("user", loggedUser);

    return "profile";
}
@GetMapping("/forgot-password")
public String forgotPasswordPage() {
    return "forgot-password";
}
@PostMapping("/send-reset-otp")
public String sendResetOtp(@RequestParam String email,
                           HttpSession session) {

    user existingUser = userRepo.findByEmail(email);

    if(existingUser == null){
        return "redirect:/forgot-password?notfound";
    }

    String otp = String.valueOf(
            (int)(Math.random()*900000)+100000);

    session.setAttribute("resetOtp", otp);
    session.setAttribute("resetEmail", email);

    emailService.sendOtp(email, otp);

    return "redirect:/verify-reset-otp";
}
@GetMapping("/verify-reset-otp")
public String verifyResetOtpPage(){
    return "verify-reset-otp";
}
@PostMapping("/verify-reset-otp")
public String verifyResetOtp(@RequestParam String otp,
                             HttpSession session){

    String savedOtp =
            (String)session.getAttribute("resetOtp");

    if(savedOtp == null || !savedOtp.equals(otp)){
        return "redirect:/verify-reset-otp?error";
    }

    return "redirect:/reset-password";
}
@GetMapping("/reset-password")
public String resetPasswordPage(){
    return "reset-password";
}
@PostMapping("/reset-password")
public String resetPassword(@RequestParam String password,
                            @RequestParam String confirmPassword,
                            HttpSession session){

    if(!password.equals(confirmPassword)){
        return "redirect:/reset-password?mismatch";
    }

    String email=(String)session.getAttribute("resetEmail");

    user existingUser=userRepo.findByEmail(email);

    existingUser.setPassword(password);

    userRepo.save(existingUser);

    session.removeAttribute("resetOtp");
    session.removeAttribute("resetEmail");

    return "redirect:/login?resetSuccess";
}
@PostMapping("/resend-reset-otp")
public String resendResetOtp(HttpSession session) {

    String email = (String) session.getAttribute("resetEmail");

    if(email == null){
        return "redirect:/forgot-password";
    }

    String otp = String.valueOf((int)(Math.random()*900000)+100000);

    session.setAttribute("resetOtp", otp);

    emailService.sendOtp(email, otp);

    return "redirect:/verify-reset-otp?resent";
}

}




