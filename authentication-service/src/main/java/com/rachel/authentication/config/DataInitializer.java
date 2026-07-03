package com.rachel.authentication.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rachel.authentication.entity.User;
import com.rachel.authentication.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init(UserRepository repository) {
        return args -> {
            if (repository.findByUsername("admin").isEmpty()) {
                repository.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .email("admin@gmail.com")
                        .role("ADMIN")
                        .phone("6283167689464")
                        .build());
            }
        };
    }
}
