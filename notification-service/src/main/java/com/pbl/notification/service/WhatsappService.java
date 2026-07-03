package com.pbl.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WhatsappService {

    private final RestTemplate restTemplate;

    @Value("${fonnte.token}")
    private String token;

    @Value("${fonnte.url}")
    private String url;

    /**
     * Mengirim pesan WhatsApp melalui Fonnte.
     *
     * @param phoneNumber format internasional, contoh: 6283167689464
     * @param message     isi pesan yang akan dikirim
     */
    public void send(String phoneNumber, String message) {
        try {
            // Header HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Body request
            Map<String, Object> body = new HashMap<>();
            body.put("target", phoneNumber);
            body.put("message", message);

            // Gabungkan header dan body
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Kirim POST ke API Fonnte
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class);

            // Log hasil response
            System.out.println("WhatsApp berhasil dikirim.");
            System.out.println("Response Fonnte: " + response.getBody());

        } catch (Exception e) {
            System.out.println(
                    "Gagal mengirim WhatsApp: " + e.getMessage());
        }
    }
}