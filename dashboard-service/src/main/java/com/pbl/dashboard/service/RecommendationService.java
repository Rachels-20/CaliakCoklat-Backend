package com.pbl.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pbl.dashboard.constant.SensorThreshold;
import com.pbl.dashboard.dto.DashboardOverviewResponse;
import com.pbl.dashboard.dto.RecommendationResponse;

@Service
public class RecommendationService {

    public RecommendationResponse generate(
            DashboardOverviewResponse overview) {

        List<String> masalah = new ArrayList<>();
        List<String> saran = new ArrayList<>();

        String status = "Baik";

        status = cekPh(
                overview,
                masalah,
                saran,
                status);

        status = cekSuhuTanah(
                overview,
                masalah,
                saran,
                status);

        status = cekSuhuUdara(
                overview,
                masalah,
                saran,
                status);

        status = cekKelembapanTanah(
                overview,
                masalah,
                saran,
                status);

        status = cekKelembapanUdara(
                overview,
                masalah,
                saran,
                status);

        String kondisi = buatKalimatKondisi(status, masalah);

        return RecommendationResponse.builder()
                .status(status)
                .kondisi(kondisi)
                .saran(saran.stream().distinct().toList())
                .build();
    }

    private String cekPh(
            DashboardOverviewResponse overview,
            List<String> masalah,
            List<String> saran,
            String status) {

        Double ph = overview.getPhTanah();

        if (ph == null) {
            return status;
        }
        if (ph < SensorThreshold.PH_PERHATIAN_MIN) {

            masalah.add("pH tanah sangat rendah");

            saran.add(
                    "Segera lakukan pengapuran untuk meningkatkan pH tanah.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (ph > SensorThreshold.PH_PERHATIAN_MAX) {

            masalah.add("pH tanah sangat tinggi");

            saran.add(
                    "Segera tambahkan bahan organik untuk membantu menurunkan pH tanah.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (ph < SensorThreshold.PH_BAIK_MIN) {

            masalah.add(
                    "pH tanah sedikit lebih rendah dari rentang ideal");

            saran.add(
                    "Lakukan pengapuran secara bertahap untuk menjaga pH tanah tetap ideal.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        if (ph > SensorThreshold.PH_BAIK_MAX) {

            masalah.add(
                    "pH tanah sedikit lebih tinggi dari rentang ideal");

            saran.add(
                    "Tambahkan bahan organik untuk membantu menurunkan pH tanah secara alami.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        return status;
    }

    private String cekSuhuTanah(
            DashboardOverviewResponse overview,
            List<String> masalah,
            List<String> saran,
            String status) {

        Double suhu = overview.getSuhuTanah();

        if (suhu == null) {
            return status;
        }
        if (suhu < SensorThreshold.SUHU_TANAH_PERHATIAN_MIN) {

            masalah.add("suhu tanah sangat rendah");

            saran.add(
                    "Segera lakukan tindakan untuk meningkatkan suhu tanah.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (suhu > SensorThreshold.SUHU_TANAH_PERHATIAN_MAX) {

            masalah.add("suhu tanah sangat tinggi");

            saran.add(
                    "Segera lakukan tindakan untuk menurunkan suhu tanah.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (suhu < SensorThreshold.SUHU_TANAH_BAIK_MIN) {

            masalah.add(
                    "suhu tanah sedikit lebih rendah dari rentang ideal");

            saran.add(
                    "Periksa kondisi penutupan lahan untuk membantu menjaga suhu tanah.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        if (suhu > SensorThreshold.SUHU_TANAH_BAIK_MAX) {

            masalah.add(
                    "suhu tanah sedikit lebih tinggi dari rentang ideal");

            saran.add(
                    "Tambahkan mulsa untuk membantu menurunkan suhu tanah.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        return status;
    }

    private String cekSuhuUdara(
            DashboardOverviewResponse overview,
            List<String> masalah,
            List<String> saran,
            String status) {

        Double suhu = overview.getSuhuUdara();

        if (suhu == null) {
            return status;
        }
        if (suhu < SensorThreshold.SUHU_UDARA_PERHATIAN_MIN) {

            masalah.add("suhu udara sangat rendah");

            saran.add(
                    "Segera lakukan pemantauan intensif terhadap tanaman yang berpotensi terdampak suhu rendah.");
            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (suhu > SensorThreshold.SUHU_UDARA_PERHATIAN_MAX) {

            masalah.add("suhu udara sangat tinggi");

            saran.add(
                    "Segera lakukan penyiraman yang memadai dan periksa kondisi tanaman yang terdampak panas berlebih.");
            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (suhu < SensorThreshold.SUHU_UDARA_BAIK_MIN) {

            masalah.add(
                    "suhu udara sedikit lebih rendah dari rentang ideal");
            saran.add(
                    "Lakukan pemantauan kondisi tanaman untuk mengantisipasi dampak suhu rendah.");
            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        if (suhu > SensorThreshold.SUHU_UDARA_BAIK_MAX) {

            masalah.add(
                    "suhu udara sedikit lebih tinggi dari rentang ideal");

            saran.add(
                    "Pastikan kebutuhan air tanaman tercukupi selama suhu udara tinggi.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        return status;
    }

    private String cekKelembapanTanah(
            DashboardOverviewResponse overview,
            List<String> masalah,
            List<String> saran,
            String status) {

        Double kelembapan = overview.getKelembapanTanah();

        if (kelembapan == null) {
            return status;
        }

        if (kelembapan < SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MIN) {

            masalah.add("kelembapan tanah sangat rendah");

            saran.add(
                    "Segera tingkatkan kelembapan tanah melalui penyiraman yang memadai.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (kelembapan > SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MAX) {

            masalah.add("kelembapan tanah sangat tinggi");

            saran.add(
                    "Segera perbaiki drainase untuk mengurangi kelebihan air pada tanah.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (kelembapan < SensorThreshold.KELEMBAPAN_TANAH_BAIK_MIN) {

            masalah.add(
                    "kelembapan tanah sedikit lebih rendah dari rentang ideal");

            saran.add(
                    "Tingkatkan frekuensi penyiraman untuk menjaga kelembapan tanah.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        if (kelembapan > SensorThreshold.KELEMBAPAN_TANAH_BAIK_MAX) {

            masalah.add(
                    "kelembapan tanah sedikit lebih tinggi dari rentang ideal");

            saran.add(
                    "Periksa drainase untuk mencegah genangan air pada lahan.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        return status;
    }

    private String cekKelembapanUdara(
            DashboardOverviewResponse overview,
            List<String> masalah,
            List<String> saran,
            String status) {

        Double kelembapan = overview.getKelembapanUdara();

        if (kelembapan == null) {
            return status;
        }

        if (kelembapan < SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MIN) {

            masalah.add("kelembapan udara sangat rendah");

            saran.add(
                    "Pertahankan kelembapan lingkungan di sekitar tanaman.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (kelembapan > SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MAX) {

            masalah.add("kelembapan udara sangat tinggi");

            saran.add(
                    "Pastikan sirkulasi udara berjalan dengan baik untuk mengurangi kelembapan berlebih.");

            return updateStatus(
                    status,
                    "Bahaya");
        }

        if (kelembapan < SensorThreshold.KELEMBAPAN_UDARA_BAIK_MIN) {

            masalah.add(
                    "kelembapan udara sedikit lebih rendah dari rentang ideal");

            saran.add(
                    "Pastikan sirkulasi udara dan kondisi lingkungan tetap terjaga.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        if (kelembapan > SensorThreshold.KELEMBAPAN_UDARA_BAIK_MAX) {

            masalah.add(
                    "kelembapan udara sedikit lebih tinggi dari rentang ideal");

            saran.add(
                    "Pastikan sirkulasi udara dan kondisi lingkungan tetap terjaga.");

            return updateStatus(
                    status,
                    "Perlu Perhatian");
        }

        return status;
    }

    private String updateStatus(
            String currentStatus,
            String newStatus) {

        if ("Bahaya".equals(currentStatus)) {
            return currentStatus;
        }

        if ("Bahaya".equals(newStatus)) {
            return "Bahaya";
        }

        if ("Perlu Perhatian".equals(newStatus)) {
            return "Perlu Perhatian";
        }

        return currentStatus;
    }

    private String buatKalimatKondisi(
            String status,
            List<String> masalah) {

        if (masalah.isEmpty()) {

            return "Kondisi kebun saat ini berada dalam kategori baik. Seluruh parameter masih berada dalam rentang yang direkomendasikan untuk tanaman kakao.";
        }

        if ("Bahaya".equals(status)) {

            return "Beberapa parameter kebun berada di luar rentang yang direkomendasikan dan memerlukan tindakan segera, yaitu "
                    + gabungkanMasalah(masalah)
                    + ".";
        }

        return "Kondisi kebun saat ini memerlukan perhatian karena "
                + gabungkanMasalah(masalah)
                + ".";
    }

    private String gabungkanMasalah(
            List<String> masalah) {

        if (masalah.size() == 1) {
            return masalah.get(0);
        }

        if (masalah.size() == 2) {
            return masalah.get(0)
                    + " dan "
                    + masalah.get(1);
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < masalah.size(); i++) {

            if (i == masalah.size() - 1) {

                sb.append("dan ")
                        .append(masalah.get(i));

            } else {

                sb.append(masalah.get(i))
                        .append(", ");
            }
        }

        return sb.toString();
    }
}