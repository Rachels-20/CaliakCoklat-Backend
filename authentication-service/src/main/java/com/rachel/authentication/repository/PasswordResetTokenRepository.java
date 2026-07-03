package com.rachel.authentication.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.rachel.authentication.entity.PasswordResetToken;

public interface PasswordResetTokenRepository
                extends JpaRepository<PasswordResetToken, Long> {

        Optional<PasswordResetToken> findByCode(String code);

        List<PasswordResetToken> findByUserIdAndUsedFalse(Long userId);

        Optional<PasswordResetToken> findByUserIdAndCode(
                        Long userId,
                        String code);

        @Modifying
        @Transactional
        void deleteByExpiredAtBefore(
                        LocalDateTime time);

}