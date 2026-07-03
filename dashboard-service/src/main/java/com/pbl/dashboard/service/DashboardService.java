package com.pbl.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.dashboard.dto.DashboardOverviewResponse;
import com.pbl.dashboard.dto.DashboardResponse;
import com.pbl.dashboard.dto.DeviceResponse;
import com.pbl.dashboard.dto.SensorResponse;
import com.pbl.dashboard.constant.SensorThreshold;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RestTemplate restTemplate;

    // Endpoint device-service (memerlukan JWT)
    private static final String DEVICE_URL = "http://device-service:8092/devices/me";

    // Endpoint sensor-service (tidak memerlukan JWT)
    private static final String SUHU_TANAH_URL = "http://suhu-tanah-service:8097/suhu-tanah/latest?deviceId=";

    private static final String SUHU_UDARA_URL = "http://suhu-udara-service:8098/suhu-udara/latest?deviceId=";

    private static final String KELEMBAPAN_TANAH_URL = "http://kelembapan-tanah-service:8094/kelembapan-tanah/latest?deviceId=";

    private static final String KELEMBAPAN_UDARA_URL = "http://kelembapan-udara-service:8095/kelembapan-udara/latest?deviceId=";

    private static final String PH_TANAH_URL = "http://ph-tanah-service:8096/ph-tanah/latest?deviceId=";

    /**
     * Mengambil seluruh data dashboard untuk user yang sedang login.
     * JWT diteruskan ke device-service agar device yang diambil hanya milik user
     * tersebut.
     */
    public List<DashboardResponse> getDashboardByUserId(Long userId, String authHeader) {

        // userId sudah divalidasi di controller melalui JwtUtil.
        // Parameter userId dipertahankan agar signature method jelas secara desain.
        if (userId == null) {
            throw new RuntimeException("User ID tidak valid");
        }

        // Kirim Authorization header ke device-service
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Ambil semua device milik user yang sedang login
        ResponseEntity<DeviceResponse[]> response = restTemplate.exchange(
                DEVICE_URL,
                HttpMethod.GET,
                entity,
                DeviceResponse[].class);

        DeviceResponse[] body = response.getBody();

        if (body == null || body.length == 0) {
            return new ArrayList<>();
        }

        List<DeviceResponse> devices = Arrays.asList(body);
        List<DashboardResponse> result = new ArrayList<>();

        // Untuk setiap device, ambil nilai sensor terbaru
        for (DeviceResponse device : devices) {
            Long deviceId = device.getId();

            Double suhuTanah = getSensorValue(SUHU_TANAH_URL + deviceId);
            Double suhuUdara = getSensorValue(SUHU_UDARA_URL + deviceId);
            Double phTanah = getSensorValue(PH_TANAH_URL + deviceId);
            Double kelembapanTanah = getSensorValue(KELEMBAPAN_TANAH_URL + deviceId);
            Double kelembapanUdara = getSensorValue(KELEMBAPAN_UDARA_URL + deviceId);

            DashboardResponse dashboard = DashboardResponse.builder()
                    .deviceId(deviceId)
                    .nama(device.getNama())
                    .lokasi(device.getLokasi())
                    .status(device.getStatus())

                    .suhuTanah(suhuTanah)

                    .suhuUdara(suhuUdara)

                    .phTanah(phTanah)

                    .kelembapanTanah(kelembapanTanah)

                    .kelembapanUdara(kelembapanUdara)

                    .build();

            result.add(dashboard);
        }

        return result;
    }

    private String getPhStatus(Double ph) {

        if (ph == null)
            return "Tidak Ada Data";

        if (ph < SensorThreshold.PH_PERHATIAN_MIN
                || ph > SensorThreshold.PH_PERHATIAN_MAX)
            return "Bahaya";

        if (ph < SensorThreshold.PH_BAIK_MIN
                || ph > SensorThreshold.PH_BAIK_MAX)
            return "Perlu Perhatian";

        return "Baik";
    }

    private String getSuhuTanahStatus(Double suhu) {

        if (suhu == null)
            return "Tidak Ada Data";

        if (suhu < SensorThreshold.SUHU_TANAH_PERHATIAN_MIN
                || suhu > SensorThreshold.SUHU_TANAH_PERHATIAN_MAX)
            return "Bahaya";

        if (suhu < SensorThreshold.SUHU_TANAH_BAIK_MIN
                || suhu > SensorThreshold.SUHU_TANAH_BAIK_MAX)
            return "Perlu Perhatian";

        return "Baik";
    }

    private String getSuhuUdaraStatus(Double suhu) {

        if (suhu == null)
            return "Tidak Ada Data";

        if (suhu < SensorThreshold.SUHU_UDARA_PERHATIAN_MIN
                || suhu > SensorThreshold.SUHU_UDARA_PERHATIAN_MAX)
            return "Bahaya";

        if (suhu < SensorThreshold.SUHU_UDARA_BAIK_MIN
                || suhu > SensorThreshold.SUHU_UDARA_BAIK_MAX)
            return "Perlu Perhatian";

        return "Baik";
    }

    private String getKelembapanTanahStatus(Double nilai) {

        if (nilai == null)
            return "Tidak Ada Data";

        if (nilai < SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MIN
                || nilai > SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MAX)
            return "Bahaya";

        if (nilai < SensorThreshold.KELEMBAPAN_TANAH_BAIK_MIN
                || nilai > SensorThreshold.KELEMBAPAN_TANAH_BAIK_MAX)
            return "Perlu Perhatian";

        return "Baik";
    }

    private String getKelembapanUdaraStatus(Double nilai) {

        if (nilai == null)
            return "Tidak Ada Data";

        if (nilai < SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MIN
                || nilai > SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MAX)
            return "Bahaya";

        if (nilai < SensorThreshold.KELEMBAPAN_UDARA_BAIK_MIN
                || nilai > SensorThreshold.KELEMBAPAN_UDARA_BAIK_MAX)
            return "Perlu Perhatian";

        return "Baik";
    }

    /**
     * Mengambil nilai sensor terbaru.
     * Jika service sensor belum memiliki data atau sedang tidak tersedia,
     * method akan mengembalikan null agar dashboard tetap dapat ditampilkan.
     */
    private Double getSensorValue(String url) {
        try {
            SensorResponse response = restTemplate.getForObject(url, SensorResponse.class);

            return response != null ? response.getNilai() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public DashboardOverviewResponse getOverviewByUserId(
            Long userId,
            String authHeader) {

        List<DashboardResponse> devices = getDashboardByUserId(userId, authHeader);

        Double suhuTanah = average(devices.stream()
                .map(DashboardResponse::getSuhuTanah)
                .toList());

        Double suhuUdara = average(devices.stream()
                .map(DashboardResponse::getSuhuUdara)
                .toList());

        Double phTanah = average(devices.stream()
                .map(DashboardResponse::getPhTanah)
                .toList());

        Double kelembapanTanah = average(devices.stream()
                .map(DashboardResponse::getKelembapanTanah)
                .toList());

        Double kelembapanUdara = average(devices.stream()
                .map(DashboardResponse::getKelembapanUdara)
                .toList());

        return DashboardOverviewResponse.builder()
                .suhuTanah(suhuTanah)
                .suhuTanahStatus(getSuhuTanahStatus(suhuTanah))
                .suhuTanahMessage(getSuhuTanahMessage(suhuTanah))

                .suhuUdara(suhuUdara)
                .suhuUdaraStatus(getSuhuUdaraStatus(suhuUdara))
                .suhuUdaraMessage(getSuhuUdaraMessage(suhuUdara))

                .phTanah(phTanah)
                .phTanahStatus(getPhStatus(phTanah))
                .phTanahMessage(getPhTanahMessage(phTanah))

                .kelembapanTanah(kelembapanTanah)
                .kelembapanTanahStatus(getKelembapanTanahStatus(kelembapanTanah))
                .kelembapanTanahMessage(getKelembapanTanahMessage(kelembapanTanah))

                .kelembapanUdara(kelembapanUdara)
                .kelembapanUdaraStatus(getKelembapanUdaraStatus(kelembapanUdara))
                .kelembapanUdaraMessage(getKelembapanUdaraMessage(kelembapanUdara))

                .build();
    }

    private String getSuhuTanahMessage(Double suhu) {

        if (suhu == null)
            return "Data suhu tanah belum tersedia.";

        if (suhu < SensorThreshold.SUHU_TANAH_PERHATIAN_MIN)
            return "Suhu tanah berada jauh di bawah kisaran yang direkomendasikan sehingga aktivitas akar dan penyerapan unsur hara dapat terganggu.";

        if (suhu < SensorThreshold.SUHU_TANAH_BAIK_MIN)
            return "Suhu tanah sedikit lebih rendah dari kisaran yang direkomendasikan sehingga penyerapan air dan unsur hara dapat menjadi kurang optimal.";

        if (suhu <= SensorThreshold.SUHU_TANAH_BAIK_MAX)
            return "Suhu tanah berada pada kisaran yang sesuai untuk budidaya kakao.";

        if (suhu <= SensorThreshold.SUHU_TANAH_PERHATIAN_MAX)
            return "Suhu tanah sedikit lebih tinggi dari kisaran yang direkomendasikan sehingga kelembapan tanah dapat berkurang lebih cepat.";

        return "Suhu tanah berada jauh di atas kisaran yang direkomendasikan sehingga kondisi perakaran dan kelembapan tanah dapat terganggu.";
    }

    private String getSuhuUdaraMessage(Double suhu) {

        if (suhu == null)
            return "Data suhu udara belum tersedia.";

        if (suhu < SensorThreshold.SUHU_UDARA_PERHATIAN_MIN)
            return "Suhu udara berada jauh di bawah kisaran yang direkomendasikan sehingga kondisi tanaman berisiko mengalami gangguan.";

        if (suhu < SensorThreshold.SUHU_UDARA_BAIK_MIN)
            return "Suhu udara sedikit lebih rendah dari kisaran yang direkomendasikan sehingga aktivitas fisiologis tanaman dapat menurun.";

        if (suhu <= SensorThreshold.SUHU_UDARA_BAIK_MAX)
            return "Suhu udara berada pada kisaran yang sesuai untuk budidaya kakao.";

        if (suhu <= SensorThreshold.SUHU_UDARA_PERHATIAN_MAX)
            return "Suhu udara sedikit lebih tinggi dari kisaran yang direkomendasikan sehingga kebutuhan air tanaman dapat meningkat.";

        return "Suhu udara berada jauh di atas kisaran yang direkomendasikan sehingga tanaman berisiko mengalami stres akibat suhu tinggi.";
    }

    private String getPhTanahMessage(Double ph) {

        if (ph == null)
            return "Data pH tanah belum tersedia.";

        if (ph < SensorThreshold.PH_PERHATIAN_MIN)
            return "pH tanah berada jauh di bawah kisaran yang direkomendasikan sehingga penyerapan unsur hara dapat terganggu.";

        if (ph < SensorThreshold.PH_BAIK_MIN)
            return "pH tanah sedikit lebih rendah dari kisaran yang direkomendasikan sehingga ketersediaan unsur hara dapat mulai berkurang.";

        if (ph <= SensorThreshold.PH_BAIK_MAX)
            return "pH tanah berada pada kisaran yang sesuai untuk budidaya kakao.";

        if (ph <= SensorThreshold.PH_PERHATIAN_MAX)
            return "pH tanah sedikit lebih tinggi dari kisaran yang direkomendasikan sehingga keseimbangan unsur hara perlu dipantau.";

        return "pH tanah berada jauh di atas kisaran yang direkomendasikan sehingga ketersediaan beberapa unsur hara dapat menurun.";
    }

    private String getKelembapanTanahMessage(Double nilai) {

        if (nilai == null)
            return "Data kelembapan tanah belum tersedia.";

        if (nilai < SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MIN)
            return "Kelembapan tanah berada jauh di bawah kisaran yang direkomendasikan sehingga tanaman berisiko mengalami kekurangan air.";

        if (nilai < SensorThreshold.KELEMBAPAN_TANAH_BAIK_MIN)
            return "Kelembapan tanah sedikit lebih rendah dari kisaran yang direkomendasikan sehingga kebutuhan air tanaman perlu diperhatikan.";

        if (nilai <= SensorThreshold.KELEMBAPAN_TANAH_BAIK_MAX)
            return "Kelembapan tanah berada pada kisaran yang sesuai untuk budidaya kakao.";

        if (nilai <= SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MAX)
            return "Kelembapan tanah sedikit lebih tinggi dari kisaran yang direkomendasikan sehingga kondisi drainase perlu dipantau.";

        return "Kelembapan tanah berada jauh di atas kisaran yang direkomendasikan sehingga akar berisiko mengalami gangguan akibat kelebihan air.";
    }

    private String getKelembapanUdaraMessage(Double nilai) {

        if (nilai == null)
            return "Data kelembapan udara belum tersedia.";

        if (nilai < SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MIN)
            return "Kelembapan udara berada jauh di bawah kisaran yang direkomendasikan sehingga tanaman berisiko mengalami kekeringan.";

        if (nilai < SensorThreshold.KELEMBAPAN_UDARA_BAIK_MIN)
            return "Kelembapan udara sedikit lebih rendah dari kisaran yang direkomendasikan sehingga tanaman lebih mudah kehilangan kelembapan.";

        if (nilai <= SensorThreshold.KELEMBAPAN_UDARA_BAIK_MAX)
            return "Kelembapan udara berada pada kisaran yang sesuai untuk budidaya kakao.";

        if (nilai <= SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MAX)
            return "Kelembapan udara sedikit lebih tinggi dari kisaran yang direkomendasikan sehingga kondisi kebun perlu dipantau.";

        return "Kelembapan udara berada jauh di atas kisaran yang direkomendasikan sehingga risiko pertumbuhan jamur dan penyakit tanaman dapat meningkat.";
    }

    private Double average(List<Double> values) {

        List<Double> validValues = values.stream()
                .filter(v -> v != null)
                .toList();

        if (validValues.isEmpty()) {
            return null;
        }

        double average = validValues.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        return Math.round(average * 10.0) / 10.0;
    }
}