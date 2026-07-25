package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {

    private final Map<String,String> otpMap = new HashMap<>();

    public String generateOTP(String phone){

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000));

        otpMap.put(phone, otp);

        return otp;
    }

    public boolean verifyOTP(String phone,String enteredOTP){

        String stored = otpMap.get(phone);

        if(stored!=null && stored.equals(enteredOTP)){
            otpMap.remove(phone);
            return true;
        }

        return false;
    }
}