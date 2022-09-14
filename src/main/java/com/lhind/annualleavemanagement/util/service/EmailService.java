package com.lhind.annualleavemanagement.util.service;

import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMailToManager(String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        CustomUserDetails currentAuthenticatedUser = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity userEntity = userService.findUserById(currentAuthenticatedUser.getId());

        if (!userEntity.getRole().equals(Constants.ROLE_EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_AN_EMPLOYEE);
        }
        simpleMailMessage.setTo(userEntity.getManager().getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        simpleMailMessage.setFrom(userEntity.getEmail());
        
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            System.out.println("Cannot send email");
        }
    }

    public void sendMailToEmployee(UserEntity employee, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        CustomUserDetails currentAuthenticatedUser = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity userEntity = userService.findUserById(currentAuthenticatedUser.getId());

        if (!userEntity.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        simpleMailMessage.setTo(employee.getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        simpleMailMessage.setFrom(userEntity.getEmail());

        try {
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            System.out.println("Cannot send email");
        }
    }
}
