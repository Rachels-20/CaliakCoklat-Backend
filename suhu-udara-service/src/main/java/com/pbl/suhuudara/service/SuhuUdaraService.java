package com.pbl.suhuudara.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.suhuudara.dto.SuhuUdaraRequest;
import com.pbl.suhuudara.dto.SuhuUdaraResponse;
import com.pbl.suhuudara.entity.SuhuUdara;
import com.pbl.suhuudara.repository.SuhuUdaraRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.pbl.suhuudara.dto.DeviceResponse;
import com.pbl.suhuudara.dto.SensorDataEvent;
import com.pbl.suhuudara.dto.SensorStatusEvent;
import com.pbl.suhuudara.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class SuhuUdaraService {

        private final SuhuUdaraRepository repository;
        private final RabbitTemplate rabbitTemplate;
        private final RestTemplate restTemplate;
        private static final String DEVICE_URL = "http://device-service:8092/devices/kode/";

        public SuhuUdaraResponse saveData(SuhuUdaraRequest request) {

                DeviceResponse device = restTemplate.getForObject(
                                DEVICE_URL + request.getKodePerangkat(),
                                DeviceResponse.class);

                if (device == null) {
                        throw new RuntimeException("Device tidak ditemukan");
                }

                // Ambil status sensor terakhir
                SuhuUdara lastData = repository.findTopByDeviceIdOrderByWaktuDesc(device.getId());

                // Apakah status sensor berubah?
                boolean statusChanged = lastData == null ||
                                lastData.isSensorOK() != request.isSensorOK();

                System.out.println(
                                "[DATA] " +
                                                request.getKodePerangkat() +
                                                " -> deviceId=" +
                                                device.getId());

                // Simpan data sensor
                SuhuUdara data = new SuhuUdara();

                data.setDeviceId(device.getId());
                data.setNilai(request.getNilai());
                data.setSensorOK(request.isSensorOK());
                data.setWaktu(LocalDateTime.now());

                SuhuUdara saved = repository.save(data);

                // ==========================
                // Event untuk threshold
                // (SELALU dikirim)
                // ==========================

                SensorDataEvent dataEvent = new SensorDataEvent();

                dataEvent.setDeviceId(saved.getDeviceId());
                dataEvent.setSensorType("SUHU_UDARA");
                dataEvent.setValue(saved.getNilai());

                rabbitTemplate.convertAndSend(
                                RabbitMQConfig.EXCHANGE,
                                RabbitMQConfig.ROUTING_KEY,
                                dataEvent);

                // ==========================
                // Event status sensor
                // (HANYA jika berubah)
                // ==========================

                if (statusChanged) {

                        SensorStatusEvent statusEvent = new SensorStatusEvent();

                        statusEvent.setDeviceId(saved.getDeviceId());
                        statusEvent.setSensorType("SUHU_UDARA");
                        statusEvent.setSensorOK(saved.isSensorOK());

                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.EXCHANGE,
                                        RabbitMQConfig.STATUS_ROUTING_KEY,
                                        statusEvent);
                }

                return SuhuUdaraResponse.builder()
                                .id(saved.getId())
                                .deviceId(saved.getDeviceId())
                                .nilai(saved.getNilai())
                                .waktu(saved.getWaktu())
                                .build();
        }

        public SuhuUdaraResponse getLatest(Long deviceId) {
                SuhuUdara latest = repository.findTopByDeviceIdOrderByWaktuDesc(deviceId);

                if (latest == null) {
                        throw new RuntimeException("Data tidak ditemukan");
                }

                return SuhuUdaraResponse.builder()
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
                        List<SuhuUdara> dailyData = repository.findByDeviceIdAndWaktuBetween(
                                        deviceId,
                                        day.atStartOfDay(),
                                        day.atTime(23, 59, 59));

                        OptionalDouble optionalAverage = dailyData.stream()
                                        .mapToDouble(SuhuUdara::getNilai)
                                        .average();

                        Double average = optionalAverage.isPresent()
                                        ? optionalAverage.getAsDouble()
                                        : null;

                        result.add(average);
                }

                return result;
        }
}