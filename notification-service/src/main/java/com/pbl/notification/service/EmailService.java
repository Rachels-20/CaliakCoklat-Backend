package com.pbl.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(
            String email,
            String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "Reset Password Caliak Coklat");

        message.setText(
                "Kode OTP Anda adalah: "
                        + code +
                        "\n\nBerlaku selama 10 menit.");

        mailSender.send(message);
    }
}