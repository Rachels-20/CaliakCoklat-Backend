package com.pbl.kelembapanudara.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.pbl.kelembapanudara.config.RabbitMQConfig;
import com.pbl.kelembapanudara.dto.DeviceResponse;
import com.pbl.kelembapanudara.dto.KelembapanUdaraRequest;
import com.pbl.kelembapanudara.dto.KelembapanUdaraResponse;
import com.pbl.kelembapanudara.dto.SensorDataEvent;
import com.pbl.kelembapanudara.dto.SensorStatusEvent;
import com.pbl.kelembapanudara.entity.KelembapanUdara;
import com.pbl.kelembapanudara.repository.KelembapanUdaraRepository;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KelembapanUdaraService {

        private final KelembapanUdaraRepository repository;
        private final RabbitTemplate rabbitTemplate;
        private final RestTemplate restTemplate;
        private static final String DEVICE_URL = "http://device-service:8092/devices/kode/";

        public KelembapanUdaraResponse saveData(KelembapanUdaraRequest request) {

                DeviceResponse device = restTemplate.getForObject(
                                DEVICE_URL + request.getKodePerangkat(),
                                DeviceResponse.class);

                if (device == null) {
                        throw new RuntimeException("Device tidak ditemukan");
                }

                // Ambil data terakhir
                KelembapanUdara lastData = repository.findTopByDeviceIdOrderByWaktuDesc(device.getId());

                // Cek apakah status sensor berubah
                boolean statusChanged = lastData != null &&
                                lastData.isSensorOK() != request.isSensorOK();

                System.out.println(
                                "[DATA] " +
                                                request.getKodePerangkat() +
                                                " -> deviceId=" +
                                                device.getId());

                // Simpan data
                KelembapanUdara data = new KelembapanUdara();

                data.setDeviceId(device.getId());
                data.setNilai(request.getNilai());
                data.setSensorOK(request.isSensorOK());
                data.setWaktu(LocalDateTime.now());

                KelembapanUdara saved = repository.save(data);

                // ==========================
                // Event data sensor (selalu)
                // ==========================
                SensorDataEvent dataEvent = new SensorDataEvent();

                dataEvent.setDeviceId(saved.getDeviceId());
                dataEvent.setSensorType("KELEMBAPAN_UDARA");
                dataEvent.setValue(saved.getNilai());

                rabbitTemplate.convertAndSend(
                                RabbitMQConfig.EXCHANGE,
                                RabbitMQConfig.ROUTING_KEY,
                                dataEvent);

                // ==========================
                // Event status sensor
                // (hanya jika berubah)
                // ==========================
                if (statusChanged) {

                        SensorStatusEvent statusEvent = new SensorStatusEvent();

                        statusEvent.setDeviceId(saved.getDeviceId());
                        statusEvent.setSensorType("KELEMBAPAN_UDARA");
                        statusEvent.setSensorOK(saved.isSensorOK());

                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.EXCHANGE,
                                        RabbitMQConfig.STATUS_ROUTING_KEY,
                                        statusEvent);
                }

                return KelembapanUdaraResponse.builder()
                                .id(saved.getId())
                                .deviceId(saved.getDeviceId())
                                .nilai(saved.getNilai())
                                .waktu(saved.getWaktu())
                                .build();
        }

        public KelembapanUdaraResponse getLatest(Long deviceId) {
                KelembapanUdara latest = repository.findTopByDeviceIdOrderByWaktuDesc(deviceId);

                if (latest == null) {
                        throw new RuntimeException("Data tidak ditemukan");
                }

                return KelembapanUdaraResponse.builder()
                                .id(latest.getId())
                                .deviceId(latest.getDeviceId())
                                .nilai(latest.getNilai())
                                .waktu(latest.getWaktu())
                                .build();
        }

        public List<Double> getWeekly(Long deviceId, String date) {
                LocalDate inputDate = LocalDate.parse(date);

                LocalDate start = inputDate.with(DayOfWeek.MONDAY);
                LocalDate end = inputDate.with(DayOfWeek.SUNDAY);

                List<Double> result = new ArrayList<>();

                for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
                        List<KelembapanUdara> dailyData = repository.findByDeviceIdAndWaktuBetween(
                                        deviceId,
                                        day.atStartOfDay(),
                                        day.atTime(23, 59, 59));

                        OptionalDouble optionalAverage = dailyData.stream()
                                        .mapToDouble(KelembapanUdara::getNilai)
                                        .average();

                        Double average = optionalAverage.isPresent()
                                        ? optionalAverage.getAsDouble()
                                        : null;

                        result.add(average);
                }

                return result;
        }
}