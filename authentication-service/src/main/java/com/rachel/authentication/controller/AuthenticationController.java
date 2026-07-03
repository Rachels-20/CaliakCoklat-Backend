package com.rachel.authentication.controller;

import com.rachel.authentication.dto.ChangePasswordRequest;
import com.rachel.authentication.dto.ForgotPasswordRequest;
import com.rachel.authentication.dto.ProfileResponse;
import com.rachel.authentication.dto.ResetPasswordRequest;
import com.rachel.authentication.dto.UpdateProfileRequest;
import com.rachel.authentication.dto.UserResponse;
import com.rachel.authentication.dto.VerifyResetCodeRequest;
import com.rachel.authentication.login.LoginRequest;
import com.rachel.authentication.login.LoginResponse;
import com.rachel.authentication.register.RegisterRequest;
import com.rachel.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(
                request.getUsername(),
                request.getPassword());
    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return authenticationService.getUserById(id);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        authenticationService.forgotPassword(request);

        return ResponseEntity.ok(
                "Kode reset telah dikirim");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyCode(
            @RequestBody VerifyResetCodeRequest request) {

        return ResponseEntity.ok(
                authenticationService.verifyCode(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authenticationService.resetPassword(request);

        return ResponseEntity.ok(
                "Password berhasil diubah");
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        return authenticationService.getProfile(userId);
    }

    @PutMapping("/profile")
    public String updateProfile(
            HttpServletRequest request,
            @RequestBody UpdateProfileRequest updateRequest) {

        Long userId = (Long) request.getAttribute("userId");

        return authenticationService.updateProfile(
                userId,
                updateRequest);
    }

    @PutMapping("/change-password")
    public String changePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequest changeRequest) {

        Long userId = (Long) request.getAttribute("userId");

        return authenticationService.changePassword(
                userId,
                changeRequest);
    }
}