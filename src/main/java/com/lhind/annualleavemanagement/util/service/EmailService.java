package com.lhind.annualleavemanagement.util.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lhind.annualleavemanagement.security.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.HasLogger;
import com.lhind.annualleavemanagement.util.enums.Role;

@Service
public class EmailService implements HasLogger {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMailToManager(String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_AN_EMPLOYEE);
        }

        simpleMailMessage.setTo(user.getManager().getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        simpleMailMessage.setFrom(user.getEmail());

        try {
            getLogger().info("Sending Email to {}", user.getManager().getFirstName());
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            getLogger().error("Cannot send email");
        }
    }

    public void sendMailToEmployee(UserEntity employee, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        simpleMailMessage.setTo(employee.getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        simpleMailMessage.setFrom(user.getEmail());

        try {
            getLogger().info("Sending Email to {}", employee.getFirstName());
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            getLogger().error("Cannot send email");
        }
    }
}
