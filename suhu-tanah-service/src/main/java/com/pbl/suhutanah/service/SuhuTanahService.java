package com.pbl.suhutanah.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.suhutanah.config.RabbitMQConfig;
import com.pbl.suhutanah.dto.DeviceResponse;
import com.pbl.suhutanah.dto.SensorDataEvent;
import com.pbl.suhutanah.dto.SensorStatusEvent;
import com.pbl.suhutanah.dto.SuhuTanahRequest;
import com.pbl.suhutanah.dto.SuhuTanahResponse;
import com.pbl.suhutanah.entity.SuhuTanah;
import com.pbl.suhutanah.repository.SuhuTanahRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuhuTanahService {

        private final SuhuTanahRepository repository;
        private final RabbitTemplate rabbitTemplate;
        private final RestTemplate restTemplate;
        private static final String DEVICE_URL = "http://device-service:8092/devices/kode/";

        public SuhuTanahResponse saveData(SuhuTanahRequest request) {

                DeviceResponse device = restTemplate.getForObject(
                                DEVICE_URL + request.getKodePerangkat(),
                                DeviceResponse.class);

                if (device == null) {
                        throw new RuntimeException("Device tidak ditemukan");
                }

                // Ambil data terakhir
                SuhuTanah lastData = repository.findTopByDeviceIdOrderByWaktuDesc(device.getId());

                // Apakah status sensor berubah?
                boolean statusChanged = lastData != null &&
                                lastData.isSensorOK() != request.isSensorOK();

                System.out.println(
                                "[DATA] " +
                                                request.getKodePerangkat() +
                                                " -> deviceId=" +
                                                device.getId());

                // Simpan data
                SuhuTanah data = new SuhuTanah();

                data.setDeviceId(device.getId());
                data.setNilai(request.getNilai());
                data.setSensorOK(request.isSensorOK());
                data.setWaktu(LocalDateTime.now());

                SuhuTanah saved = repository.save(data);

                // ==========================
                // Event data sensor (selalu)
                // ==========================
                SensorDataEvent dataEvent = new SensorDataEvent();

                dataEvent.setDeviceId(saved.getDeviceId());
                dataEvent.setSensorType("SUHU_TANAH");
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
                        statusEvent.setSensorType("SUHU_TANAH");
                        statusEvent.setSensorOK(saved.isSensorOK());

                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.EXCHANGE,
                                        RabbitMQConfig.STATUS_ROUTING_KEY,
                                        statusEvent);
                }

                return SuhuTanahResponse.builder()
                                .id(saved.getId())
                                .deviceId(saved.getDeviceId())
                                .nilai(saved.getNilai())
                                .waktu(saved.getWaktu())
                                .build();
        }

        public SuhuTanahResponse getLatest(Long deviceId) {
                SuhuTanah latest = repository.findTopByDeviceIdOrderByWaktuDesc(deviceId);

                if (latest == null) {
                        throw new RuntimeException("Data tidak ditemukan");
                }

                return SuhuTanahResponse.builder()
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
                        List<SuhuTanah> dailyData = repository.findByDeviceIdAndWaktuBetween(
                                        deviceId,
                                        day.atStartOfDay(),
                                        day.atTime(23, 59, 59));

                        OptionalDouble optionalAverage = dailyData.stream()
                                        .mapToDouble(SuhuTanah::getNilai)
                                        .average();

                        Double average = optionalAverage.isPresent()
                                        ? optionalAverage.getAsDouble()
                                        : null;

                        result.add(average);
                }

                return result;
        }
}