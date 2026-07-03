package com.rachel.authentication.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rachel.authentication.repository.PasswordResetTokenRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpCleanupService {

    private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {

        tokenRepository.deleteByExpiredAtBefore(
                LocalDateTime.now());

        System.out.println(
                "[OTP] Expired token dibersihkan");
    }
}