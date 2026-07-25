package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    // ✅ OTP EMAIL
    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(toEmail);
        mail.setSubject("Bus Booking OTP Verification");

        mail.setText(
                "Your OTP for Bus Ticket Booking is: " + otp + "\n\n" +
                "This OTP is valid for a few minutes.\n" +
                "Do not share it with anyone."
        );

        sender.send(mail);
    }

    // ✅ TICKET EMAIL (your existing method)
    public void sendTicket(Ticket ticket){

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(ticket.getEmail());
        mail.setSubject("Bus Ticket Confirmation");

        mail.setText(
                "Ticket Booked Successfully\n\n"+
                "Passenger : "+ticket.getPassname()+"\n"+
                "Bus : "+ticket.getBusName()+"\n"+
                "Source : "+ticket.getSource()+"\n"+
                "Destination : "+ticket.getDest()+"\n"+
                "Journey Date : "+ticket.getJourneyDate()+"\n"+
                "Seat : "+ticket.getSeatNo()+"\n"+
                "Fare : ₹"+ticket.getFare()+"\n"+
                "Status : "+ticket.getStatus()
        );

        sender.send(mail);
    }
}
