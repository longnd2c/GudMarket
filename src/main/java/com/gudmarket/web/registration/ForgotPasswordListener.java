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
public class ForgotPasswordListener implements ApplicationListener<PasswordResetCompleteEvent>{
	@Autowired
    private UserService service;
 
    @Autowired
    private MessageSource messages;
 
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;
 
    @Override
    public void onApplicationEvent(PasswordResetCompleteEvent event) {
        this.forgotPassword(event);
    }
 
    private void forgotPassword(final PasswordResetCompleteEvent event) {
        Account user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }
    
    private SimpleMailMessage constructEmailMessage(PasswordResetCompleteEvent event,  Account user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Reset Password";
        String confirmationUrl = event.getAppUrl() + "/resetPassword?token=" + token;
        String message = messages.getMessage("message.regSuccLink", null, "You have submitted a PASSWORD RESET request. To continue, click on the link below.", event.getLocale());
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
