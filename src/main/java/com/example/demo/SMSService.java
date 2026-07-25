package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SMSService {

    private final String AUTH_KEY="YOUR_MSG91_AUTH_KEY";

    public void sendOTP(String phone,String otp){

        String url =
        "https://control.msg91.com/api/v5/otp?template_id=YOUR_TEMPLATE_ID"
        +"&mobile=91"+phone
        +"&authkey="+AUTH_KEY
        +"&otp="+otp;

        RestTemplate restTemplate=new RestTemplate();

        restTemplate.getForObject(url,String.class);
    }
}