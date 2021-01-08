package com.gudmarket.web.registration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.service.UserService;

@Component
public class ChangeEmailListener implements ApplicationListener<OnChangeEmailCompleteEvent>{
	@Autowired
    private UserService service;
 
    @Autowired
    private MessageSource messages;
 
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;
 
    @Override
    public void onApplicationEvent(OnChangeEmailCompleteEvent event) {
        this.confirmRegistration(event);
    }
 
    private void confirmRegistration(final OnChangeEmailCompleteEvent event) {
        Account user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }
    
    private SimpleMailMessage constructEmailMessage(OnChangeEmailCompleteEvent event,  Account user, String token) {
        String recipientAddress = event.getEmail();
        String subject = "Change/Add Email Comfirm";
        String confirmationUrl = event.getAppUrl() + "/changeEmailConfirm?token=" + token+"&email="+recipientAddress;
        String message = messages.getMessage("message.regSuccLink", null, "You sent a require to Change/Add new Email address. To confirm your action, please click on the below link.", event.getLocale());
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
