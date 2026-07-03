package com.rachel.authentication.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rachel.authentication.dto.ChangePasswordRequest;
import com.rachel.authentication.dto.ForgotPasswordRequest;
import com.rachel.authentication.dto.ProfileResponse;
import com.rachel.authentication.dto.ResetPasswordEvent;
import com.rachel.authentication.dto.ResetPasswordRequest;
import com.rachel.authentication.dto.UpdateProfileRequest;
import com.rachel.authentication.dto.UserResponse;
import com.rachel.authentication.dto.VerifyResetCodeRequest;
import com.rachel.authentication.entity.PasswordResetToken;
import com.rachel.authentication.entity.User;
import com.rachel.authentication.login.LoginResponse;
import com.rachel.authentication.register.RegisterRequest;
import com.rachel.authentication.repository.PasswordResetTokenRepository;
import com.rachel.authentication.repository.UserRepository;
import com.rachel.authentication.security.JwtUtil;
import com.rachel.authentication.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
        private final UserRepository userRepository;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final PasswordResetTokenRepository tokenRepository;
        private final RabbitTemplate rabbitTemplate;

        public String register(RegisterRequest request) {
                if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                        throw new RuntimeException("Username sudah digunakan");
                }

                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Email sudah digunakan");
                }

                String normalizedPhone = normalizePhone(request.getPhone());

                if (userRepository.findByPhone(
                                normalizedPhone).isPresent()) {

                        throw new RuntimeException(
                                        "Nomor HP sudah digunakan");
                }

                User user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role("USER")
                                .phone(normalizedPhone)
                                .build();

                userRepository.save(user);
                return "Registrasi berhasil";
        }

        public LoginResponse login(String username, String password) {

                User user = userRepository
                                .findByUsernameOrEmail(
                                                username,
                                                username)
                                .orElseThrow(() -> new RuntimeException(
                                                "Username/email atau password salah"));

                if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new RuntimeException(
                                        "Username/email atau password salah");
                }

                String token = jwtUtil.generateToken(
                                user.getId(),
                                user.getUsername(),
                                user.getRole(),
                                user.getEmail());

                return new LoginResponse(
                                token,
                                user.getUsername(),
                                user.getEmail());
        }

        public UserResponse getUserById(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                return UserResponse.builder()
                                .id(user.getId())
                                .name(user.getUsername())
                                .phoneNumber(user.getPhone())
                                .build();
        }

        public void forgotPassword(
                        ForgotPasswordRequest request) {

                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

                String code = String.valueOf(
                                ThreadLocalRandom.current()
                                                .nextInt(100000, 999999));

                List<PasswordResetToken> activeTokens = tokenRepository.findByUserIdAndUsedFalse(
                                user.getId());

                for (PasswordResetToken t : activeTokens) {
                        t.setUsed(true);
                }

                tokenRepository.saveAll(activeTokens);

                PasswordResetToken token = PasswordResetToken.builder()
                                .userId(user.getId())
                                .code(code)
                                .expiredAt(
                                                LocalDateTime.now()
                                                                .plusMinutes(10))
                                .used(false)
                                .build();

                tokenRepository.save(token);

                ResetPasswordEvent event = new ResetPasswordEvent();

                event.setEmail(user.getEmail());
                event.setCode(code);

                rabbitTemplate.convertAndSend(
                                RabbitMQConfig.EXCHANGE,
                                RabbitMQConfig.RESET_PASSWORD_ROUTING_KEY,
                                event);
        }

        public boolean verifyCode(
                        VerifyResetCodeRequest request) {

                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                PasswordResetToken token = tokenRepository
                                .findByUserIdAndCode(
                                                user.getId(),
                                                request.getCode())
                                .orElseThrow(() -> new RuntimeException("Kode tidak valid"));

                if (token.getUsed()) {
                        throw new RuntimeException(
                                        "Kode sudah digunakan");
                }

                if (token.getExpiredAt()
                                .isBefore(LocalDateTime.now())) {

                        throw new RuntimeException(
                                        "Kode sudah kadaluarsa");
                }

                return true;
        }

        public void resetPassword(
                        ResetPasswordRequest request) {

                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                PasswordResetToken token = tokenRepository
                                .findByUserIdAndCode(
                                                user.getId(),
                                                request.getCode())
                                .orElseThrow(() -> new RuntimeException("Kode tidak valid"));

                if (token.getUsed()) {
                        throw new RuntimeException(
                                        "Kode sudah digunakan");
                }

                if (token.getExpiredAt()
                                .isBefore(LocalDateTime.now())) {

                        throw new RuntimeException(
                                        "Kode sudah kadaluarsa");
                }

                user.setPassword(
                                passwordEncoder.encode(
                                                request.getNewPassword()));

                userRepository.save(user);

                tokenRepository.delete(token);
        }

        public ProfileResponse getProfile(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                return ProfileResponse.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .profileImage(user.getProfileImage())
                                .build();
        }

        public String updateProfile(
                        Long userId,
                        UpdateProfileRequest request) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                if (request.getUsername() != null &&
                                !request.getUsername().isBlank()) {

                        User existing = userRepository
                                        .findByUsername(request.getUsername())
                                        .orElse(null);

                        if (existing != null &&
                                        !existing.getId().equals(user.getId())) {

                                throw new RuntimeException(
                                                "Username sudah digunakan");
                        }

                        user.setUsername(request.getUsername());
                }

                if (request.getEmail() != null &&
                                !request.getEmail().isBlank()) {

                        User existing = userRepository
                                        .findByEmail(request.getEmail())
                                        .orElse(null);

                        if (existing != null &&
                                        !existing.getId().equals(user.getId())) {

                                throw new RuntimeException(
                                                "Email sudah digunakan");
                        }

                        user.setEmail(request.getEmail());
                }

                if (request.getPhone() != null &&
                                !request.getPhone().isBlank()) {

                        String normalizedPhone = normalizePhone(request.getPhone());

                        User existing = userRepository
                                        .findByPhone(normalizedPhone)
                                        .orElse(null);

                        if (existing != null &&
                                        !existing.getId().equals(user.getId())) {

                                throw new RuntimeException(
                                                "Nomor HP sudah digunakan");
                        }

                        user.setPhone(normalizedPhone);
                }
                if (request.getProfileImage() != null &&
                                !request.getProfileImage().isBlank()) {

                        user.setProfileImage(
                                        request.getProfileImage());
                }

                userRepository.save(user);

                return "Profil berhasil diperbarui";
        }

        private String normalizePhone(String phone) {

                phone = phone.trim();

                if (phone.startsWith("08")) {
                        return "62" + phone.substring(1);
                }

                return phone;
        }

        public String changePassword(
                        Long userId,
                        ChangePasswordRequest request) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "User tidak ditemukan"));

                if (!passwordEncoder.matches(
                                request.getCurrentPassword(),
                                user.getPassword())) {

                        throw new RuntimeException(
                                        "Password saat ini salah");
                }

                if (!request.getNewPassword()
                                .equals(request.getConfirmPassword())) {

                        throw new RuntimeException(
                                        "Konfirmasi password tidak cocok");
                }

                if (request.getNewPassword().length() < 8) {

                        throw new RuntimeException(
                                        "Password minimal 8 karakter");
                }

                user.setPassword(
                                passwordEncoder.encode(
                                                request.getNewPassword()));

                userRepository.save(user);

                return "Password berhasil diubah";
        }
}
