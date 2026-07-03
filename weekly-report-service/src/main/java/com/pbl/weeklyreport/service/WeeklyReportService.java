package com.pbl.weeklyreport.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.DayOfWeek;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.weeklyreport.dto.DailyAverageResponse;
import com.pbl.weeklyreport.dto.DeviceResponse;
import com.pbl.weeklyreport.dto.SensorWeeklyReport;
import com.pbl.weeklyreport.dto.WeeklyReportResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final RestTemplate restTemplate;

    // Device service
    private static final String DEVICE_URL = "http://device-service:8092/devices/me";

    // Sensor weekly endpoints
    private static final String SUHU_TANAH_URL = "http://suhu-tanah-service:8097/suhu-tanah/weekly?deviceId=";

    private static final String SUHU_UDARA_URL = "http://suhu-udara-service:8098/suhu-udara/weekly?deviceId=";

    private static final String KELEMBAPAN_TANAH_URL = "http://kelembapan-tanah-service:8094/kelembapan-tanah/weekly?deviceId=";

    private static final String KELEMBAPAN_UDARA_URL = "http://kelembapan-udara-service:8095/kelembapan-udara/weekly?deviceId=";

    private static final String PH_TANAH_URL = "http://ph-tanah-service:8096/ph-tanah/weekly?deviceId=";

    public WeeklyReportResponse getWeeklyReport(
            String authHeader,
            String date) {
        // Ambil seluruh device milik user
        List<DeviceResponse> devices = getUserDevices(authHeader);

        // Ambil data mingguan dari seluruh device
        List<List<Double>> suhuTanahData = collectSensorData(devices, SUHU_TANAH_URL, date);
        List<List<Double>> suhuUdaraData = collectSensorData(devices, SUHU_UDARA_URL, date);
        List<List<Double>> kelembapanTanahData = collectSensorData(devices, KELEMBAPAN_TANAH_URL, date);
        List<List<Double>> kelembapanUdaraData = collectSensorData(devices, KELEMBAPAN_UDARA_URL, date);
        List<List<Double>> phTanahData = collectSensorData(devices, PH_TANAH_URL, date);

        LocalDate today;

        if (date != null && !date.isBlank()) {
            today = LocalDate.parse(date);
        } else {
            today = LocalDate.now();
        }

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        LocalDate weekEnd = weekStart.plusDays(6);

        return WeeklyReportResponse.builder()
                .weekStart(weekStart.toString())
                .weekEnd(weekEnd.toString())
                .suhuTanah(buildSensorReport(suhuTanahData, today))
                .suhuUdara(buildSensorReport(suhuUdaraData, today))
                .kelembapanTanah(buildSensorReport(kelembapanTanahData, today))
                .kelembapanUdara(buildSensorReport(kelembapanUdaraData, today))
                .phTanah(buildSensorReport(phTanahData, today))
                .build();
    }

    /**
     * Ambil seluruh device milik user.
     */
    private List<DeviceResponse> getUserDevices(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<DeviceResponse[]> response = restTemplate.exchange(
                DEVICE_URL,
                HttpMethod.GET,
                entity,
                DeviceResponse[].class);

        DeviceResponse[] body = response.getBody();

        if (body == null || body.length == 0) {
            return new ArrayList<>();
        }

        return Arrays.asList(body);
    }

    /**
     * Ambil data mingguan dari seluruh device.
     * Setiap sensor service mengembalikan List<Double> berisi 7 elemen
     * (Senin sampai Minggu).
     */
    private List<List<Double>> collectSensorData(
            List<DeviceResponse> devices,
            String baseUrl,
            String date) {

        List<List<Double>> result = new ArrayList<>();

        for (DeviceResponse device : devices) {
            try {
                String url = baseUrl + device.getId();

                if (date != null && !date.isBlank()) {
                    url += "&date=" + date;
                }

                System.out.println("REQUEST URL = " + url);

                ResponseEntity<List<Double>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Double>>() {
                        });

                List<Double> data = response.getBody();

                System.out.println("DEVICE ID = " + device.getId());
                System.out.println("DATA = " + data);

                if (data != null) {
                    result.add(data);
                }
            } catch (Exception e) {
                System.out.println("ERROR DEVICE ID = " + device.getId());
                e.printStackTrace();
            }
        }

        System.out.println("TOTAL DATA SIZE = " + result.size());

        return result;
    }

    /**
     * Hitung rata-rata mingguan dan bentuk data harian untuk grafik.
     */
    private SensorWeeklyReport buildSensorReport(
            List<List<Double>> deviceData,
            LocalDate today) {

        if (deviceData == null || deviceData.isEmpty()) {
            return SensorWeeklyReport.builder()
                    .averageWeekly(null)
                    .dailyAverages(new ArrayList<>())
                    .build();
        }

        List<DailyAverageResponse> dailyAverages = new ArrayList<>();

        LocalDate startDate = today.with(DayOfWeek.MONDAY);

        List<Double> weeklyValues = new ArrayList<>();

        for (int day = 0; day < 7; day++) {

            List<Double> values = new ArrayList<>();

            for (List<Double> device : deviceData) {

                if (device != null
                        && day < device.size()
                        && device.get(day) != null) {

                    values.add(device.get(day));
                }
            }

            Double average = null;

            if (!values.isEmpty()) {

                average = values.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0);

                average = Math.round(average * 10.0) / 10.0;

                weeklyValues.add(average);
            }

            dailyAverages.add(
                    DailyAverageResponse.builder()
                            .date(startDate.plusDays(day).toString())
                            .average(average)
                            .build());
        }

        Double weeklyAverage = null;

        if (!weeklyValues.isEmpty()) {

            weeklyAverage = weeklyValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

            weeklyAverage = Math.round(weeklyAverage * 10.0) / 10.0;
        }

        return SensorWeeklyReport.builder()
                .averageWeekly(weeklyAverage)
                .dailyAverages(dailyAverages)
                .build();
    }
}