package com.gudmarket.web.phone;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.gudmarket.web.entity.*;
import com.gudmarket.web.repository.TokenRepository;

@Service
public class SendSMS {
	@Value("${phoneNumber}")
    private String myTwilioPhoneNumber;
	@Autowired
	private TokenRepository tokenRepo;
		

    public void sendMessages(String phone, Account user) {
    	String  number="+84"+phone.substring(1);
    	String code =Integer.toString(generateCode());
    	String m="Your Code: "+code;
    	Token token=new Token();
    	token.setUser(user);
    	token.setToken(code);
    	tokenRepo.save(token);
            Message message = Message.creator(
                new PhoneNumber(number),
                new PhoneNumber(myTwilioPhoneNumber),m
                ).create();
            System.out.println("Sent message w/ sid: " + message.getSid());
        };
        
    public int generateCode() {
    	Random r = new Random( System.currentTimeMillis() );
        int number= 10000 + r.nextInt(20000);
    	return number;
    }
}
