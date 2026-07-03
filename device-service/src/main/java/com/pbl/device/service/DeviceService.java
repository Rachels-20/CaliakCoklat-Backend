package com.pbl.device.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.device.dto.DeviceRequest;
import com.pbl.device.dto.DeviceResponse;
import com.pbl.device.dto.IntervalResponse;
import com.pbl.device.dto.RegisterDeviceRequest;
import com.pbl.device.dto.UpdateDeviceRequest;
import com.pbl.device.dto.UserResponse;
import com.pbl.device.entity.Device;
import com.pbl.device.repository.DeviceRepository;
import com.pbl.device.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.server.ResponseStatusException;
import com.pbl.device.dto.ClaimDeviceRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {

        private final DeviceRepository deviceRepository;
        private final JwtUtil jwtUtil;
        private final RestTemplate restTemplate;

        private static final String AUTH_SERVICE_URL = "http://authentication-service:8091/auth/users/";

        /**
         * Menambahkan perangkat baru milik user yang sedang login.
         */
        public DeviceResponse createDevice(String authHeader, DeviceRequest request) {

                // Validasi Authorization header
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authorization header tidak valid");
                }

                // Ambil token tanpa prefix "Bearer "
                String token = authHeader.substring(7);

                // Extract userId dari JWT
                Long userId = jwtUtil.extractUserId(token);

                // Pastikan kode perangkat unik
                if (deviceRepository.findByKodePerangkat(
                                request.getKodePerangkat()).isPresent()) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Kode perangkat sudah digunakan");
                }

                // Buat entity
                Device device = new Device();

                device.setUserId(userId);

                device.setKodePerangkat(
                                request.getKodePerangkat());

                device.setKodeAktivasi(
                                request.getKodeAktivasi());

                device.setNama(
                                request.getNama());

                device.setLokasi(
                                request.getLokasi());
                // Simpan ke database

                System.out.println(
                                "[CREATE] User "
                                                + userId
                                                + " membuat device "
                                                + request.getKodePerangkat());
                Device saved = deviceRepository.save(device);

                // Mapping entity -> DTO
                DeviceResponse response = new DeviceResponse();
                BeanUtils.copyProperties(saved, response);

                return response;
        }

        /**
         * Mengambil semua perangkat milik user yang sedang login.
         */
        public List<DeviceResponse> getMyDevices(String authHeader) {

                // Validasi Authorization header
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authorization header tidak valid");
                }

                // Extract userId dari JWT
                String token = authHeader.substring(7);
                Long userId = jwtUtil.extractUserId(token);

                // Ambil semua device milik user
                List<Device> devices = deviceRepository.findByUserId(userId);

                // Mapping entity -> DTO
                return devices.stream()
                                .map(device -> {
                                        DeviceResponse response = new DeviceResponse();
                                        BeanUtils.copyProperties(device, response);
                                        return response;
                                })
                                .collect(Collectors.toList());
        }

        /**
         * Mengambil detail device berdasarkan ID
         * sekaligus mengambil nomor WhatsApp pemilik device
         * dari authentication-service.
         */
        public DeviceResponse getById(Long id) {

                // Cari device
                Optional<Device> optionalDevice = deviceRepository.findById(id);

                if (optionalDevice.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Device tidak ditemukan");
                }

                Device device = optionalDevice.get();

                // Ambil phone number dari authentication-service
                String phoneNumber = null;

                if (device.getUserId() != null) {

                        try {

                                UserResponse user = restTemplate.getForObject(
                                                AUTH_SERVICE_URL + device.getUserId(),
                                                UserResponse.class);
                                System.out.println("USER = " + user);

                                if (user != null) {
                                        System.out.println("PHONE = " + user.getPhoneNumber());
                                }

                                if (user != null) {
                                        phoneNumber = user.getPhoneNumber();
                                }

                        } catch (Exception e) {

                                System.out.println(
                                                "Gagal mengambil phone number: "
                                                                + e.getMessage());
                        }
                }

                // Copy semua field dari entity ke DTO
                DeviceResponse response = new DeviceResponse();
                BeanUtils.copyProperties(device, response);

                // Tambahkan field ekstra yang tidak ada di entity
                response.setPhoneNumber(phoneNumber);
                return response;
        }

        public DeviceResponse registerDevice(RegisterDeviceRequest request) {

                System.out.println(
                                "[REGISTER] Request masuk -> kodePerangkat="
                                                + request.getKodePerangkat());

                Optional<Device> existing = deviceRepository.findByKodePerangkat(
                                request.getKodePerangkat());

                if (existing.isEmpty()) {

                        System.out.println(
                                        "[REGISTER] Device baru dibuat -> "
                                                        + request.getKodePerangkat());

                        Device device = new Device();

                        device.setKodePerangkat(
                                        request.getKodePerangkat());

                        device.setKodeAktivasi(
                                        request.getKodeAktivasi());

                        device.setUserId(null);

                        device.setLastSeen(LocalDateTime.now());

                        Device saved = deviceRepository.save(device);

                        System.out.println(
                                        "[REGISTER] Berhasil disimpan -> id="
                                                        + saved.getId());

                        DeviceResponse response = new DeviceResponse();
                        BeanUtils.copyProperties(saved, response);

                        return response;
                }

                Device device = existing.get();
                System.out.println(
                                "[REGISTER] Device ditemukan -> "
                                                + request.getKodePerangkat());

                if (!request.getKodeAktivasi()
                                .equals(device.getKodeAktivasi())) {

                        System.out.println(
                                        "[REGISTER] Kode aktivasi salah -> "
                                                        + request.getKodePerangkat());

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Kode aktivasi tidak cocok");
                }

                device.setLastSeen(LocalDateTime.now());

                deviceRepository.save(device);

                System.out.println(
                                "[REGISTER] LastSeen diperbarui -> "
                                                + request.getKodePerangkat());

                DeviceResponse response = new DeviceResponse();
                BeanUtils.copyProperties(device, response);

                return response;
        }

        public DeviceResponse claimDevice(
                        String authHeader,
                        ClaimDeviceRequest request) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authorization header tidak valid");
                }

                String token = authHeader.substring(7);

                Long userId = jwtUtil.extractUserId(token);

                Device device = deviceRepository
                                .findByKodePerangkat(
                                                request.getKodePerangkat())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Device tidak ditemukan"));

                if (!request.getKodeAktivasi()
                                .equals(device.getKodeAktivasi())) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Kode aktivasi tidak cocok");
                }

                if (device.getUserId() != null) {

                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Device sudah diklaim");
                }

                device.setUserId(userId);

                device.setNama(
                                request.getNama());

                device.setLokasi(
                                request.getLokasi());

                Device saved = deviceRepository.save(device);

                System.out.println(
                                "[CLAIM] User "
                                                + userId
                                                + " claim device "
                                                + device.getKodePerangkat());

                DeviceResponse response = new DeviceResponse();

                BeanUtils.copyProperties(
                                saved,
                                response);

                return response;
        }

        public DeviceResponse unclaimDevice(
                        String authHeader,
                        Long id) {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authorization header tidak valid");
                }

                String token = authHeader.substring(7);

                Long userId = jwtUtil.extractUserId(token);

                Device device = deviceRepository
                                .findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Device tidak ditemukan"));

                if (device.getUserId() == null) {

                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Device belum diklaim");
                }

                if (!userId.equals(device.getUserId())) {

                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Bukan pemilik device");
                }

                device.setUserId(null);

                device.setNama(null);

                device.setLokasi(null);

                Device saved = deviceRepository.save(device);

                System.out.println(
                                "[UNCLAIM] User "
                                                + userId
                                                + " melepas device "
                                                + device.getKodePerangkat());

                DeviceResponse response = new DeviceResponse();

                BeanUtils.copyProperties(
                                saved,
                                response);

                return response;
        }

        public DeviceResponse getByKodePerangkat(
                        String kodePerangkat) {

                Device device = deviceRepository
                                .findByKodePerangkat(kodePerangkat)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Device tidak ditemukan"));

                DeviceResponse response = new DeviceResponse();

                BeanUtils.copyProperties(
                                device,
                                response);

                return response;
        }

        public void heartbeat(String kodePerangkat) {

                Device device = deviceRepository
                                .findByKodePerangkat(kodePerangkat)
                                .orElseThrow(() -> new RuntimeException(
                                                "Device tidak ditemukan"));

                device.setLastSeen(LocalDateTime.now());

                // Jika sebelumnya offline, ubah kembali menjadi online
                device.setAktif(true);

                deviceRepository.save(device);
        }

        public DeviceResponse updateDevice(
                        String authHeader,
                        Long id,
                        UpdateDeviceRequest request) {

                if (authHeader == null
                                || !authHeader.startsWith("Bearer ")) {

                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authorization header tidak valid");
                }

                String token = authHeader.substring(7);

                Long userId = jwtUtil.extractUserId(token);

                Device device = deviceRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Perangkat tidak ditemukan"));

                if (device.getUserId() == null) {

                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Device belum diklaim");
                }

                if (!userId.equals(device.getUserId())) {

                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Bukan pemilik perangkat");
                }

                device.setNama(request.getNama());
                device.setLokasi(request.getLokasi());
                device.setIntervalPengiriman(request.getIntervalPengiriman());

                Device saved = deviceRepository.save(device);

                DeviceResponse response = new DeviceResponse();

                BeanUtils.copyProperties(
                                saved,
                                response);

                return response;
        }

        public IntervalResponse getIntervalPengiriman(
                        String kodePerangkat) {

                Device device = deviceRepository
                                .findByKodePerangkat(kodePerangkat)
                                .orElseThrow(() -> new RuntimeException("Device tidak ditemukan"));

                return new IntervalResponse(
                                device.getIntervalPengiriman());
        }

        public void updateLastSeen(String kodePerangkat) {

                Device device = deviceRepository.findByKodePerangkat(kodePerangkat)
                                .orElseThrow(() -> new RuntimeException("Device tidak ditemukan"));

                device.setLastSeen(LocalDateTime.now());

                deviceRepository.save(device);
        }
}