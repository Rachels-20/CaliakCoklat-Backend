package com.pbl.notification.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pbl.notification.constant.SensorThreshold;
import com.pbl.notification.dto.DeviceResponse;
import com.pbl.notification.dto.SensorDataEvent;
import com.pbl.notification.dto.SensorStatusEvent;
import com.pbl.notification.entity.Notification;
import com.pbl.notification.repository.NotificationRepository;
import com.pbl.notification.dto.DeviceOfflineEvent;
import com.pbl.notification.dto.DeviceOnlineEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;
    private final NotificationRepository notificationRepository;
    private final WhatsappService whatsappService;

    private static final String DEVICE_SERVICE_URL = "http://device-service:8092/devices/";

    public void process(SensorDataEvent event) {
        try {
            // Ambil informasi device dari device-service
            DeviceResponse device = restTemplate.getForObject(
                    DEVICE_SERVICE_URL + event.getDeviceId(),
                    DeviceResponse.class);

            if (device == null) {
                return;
            }

            // Evaluasi apakah data sensor menghasilkan peringatan
            String message = evaluate(
                    event.getSensorType(),
                    event.getValue(),
                    device.getNama());

            // Jika ada peringatan, simpan ke database
            if (message != null) {
                String severity = determineSeverity(
                        event.getSensorType(),
                        event.getValue());

                System.out.println(
                        severity + ": " + message);
                Notification notification = new Notification();
                notification.setUserId(device.getUserId());
                notification.setDeviceId(event.getDeviceId());
                notification.setTitle(
                        getNotificationTitle(
                                event.getSensorType()));
                notification.setMessage(message);
                notification.setSeverity(severity);
                notification.setIsRead(false);

                notificationRepository.save(notification);

                if ("BAHAYA".equals(severity)) {
                    String phoneNumber = device.getPhoneNumber();

                    System.out.println(
                            "Device UserId: " + device.getUserId());

                    System.out.println(
                            "Phone Number: [" + phoneNumber + "]");
                    if (phoneNumber != null
                            && !phoneNumber.isBlank()) {

                        String whatsappMessage = "🚨 BAHAYA - Caliak Coklat\n\n"
                                + message;

                        whatsappService.send(
                                phoneNumber,
                                whatsappMessage);

                    } else {

                        System.out.println(
                                "Nomor WhatsApp user tidak tersedia.");
                    }
                }

            }

        } catch (Exception e) {
            System.out.println(
                    "Gagal memproses notifikasi: " + e.getMessage());
        }
    }

    /**
     * Mengevaluasi nilai sensor.
     * Jika melewati batas aman, kembalikan pesan peringatan.
     * Jika masih dalam batas normal, kembalikan null.
     */
    private String evaluate(
            String sensorType,
            Double value,
            String deviceName) {

        if ("KELEMBAPAN_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MIN) {
                return deviceName
                        + " memiliki kelembapan tanah sangat rendah.";
            }

            if (value > SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MAX) {
                return deviceName
                        + " memiliki kelembapan tanah sangat tinggi.";
            }

            if (value < SensorThreshold.KELEMBAPAN_TANAH_BAIK_MIN) {
                return deviceName
                        + " memiliki kelembapan tanah rendah.";
            }

            if (value > SensorThreshold.KELEMBAPAN_TANAH_BAIK_MAX) {
                return deviceName
                        + " memiliki kelembapan tanah tinggi.";
            }
        }

        if ("KELEMBAPAN_UDARA".equals(sensorType)) {

            if (value < SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MIN) {
                return deviceName
                        + " memiliki kelembapan udara sangat rendah.";
            }

            if (value > SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MAX) {
                return deviceName
                        + " memiliki kelembapan udara sangat tinggi.";
            }

            if (value < SensorThreshold.KELEMBAPAN_UDARA_BAIK_MIN) {
                return deviceName
                        + " memiliki kelembapan udara rendah.";
            }

            if (value > SensorThreshold.KELEMBAPAN_UDARA_BAIK_MAX) {
                return deviceName
                        + " memiliki kelembapan udara tinggi.";
            }
        }

        if ("PH_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.PH_PERHATIAN_MIN) {
                return deviceName
                        + " memiliki pH tanah sangat rendah.";
            }

            if (value > SensorThreshold.PH_PERHATIAN_MAX) {
                return deviceName
                        + " memiliki pH tanah sangat tinggi.";
            }

            if (value < SensorThreshold.PH_BAIK_MIN) {
                return deviceName
                        + " memiliki pH tanah rendah.";
            }

            if (value > SensorThreshold.PH_BAIK_MAX) {
                return deviceName
                        + " memiliki pH tanah tinggi.";
            }
        }
        if ("SUHU_UDARA".equals(sensorType)) {

            if (value < SensorThreshold.SUHU_UDARA_PERHATIAN_MIN) {
                return deviceName
                        + " memiliki suhu udara sangat rendah.";
            }

            if (value > SensorThreshold.SUHU_UDARA_PERHATIAN_MAX) {
                return deviceName
                        + " memiliki suhu udara sangat tinggi.";
            }

            if (value < SensorThreshold.SUHU_UDARA_BAIK_MIN) {
                return deviceName
                        + " memiliki suhu udara rendah.";
            }

            if (value > SensorThreshold.SUHU_UDARA_BAIK_MAX) {
                return deviceName
                        + " memiliki suhu udara tinggi.";
            }
        }
        if ("SUHU_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.SUHU_TANAH_PERHATIAN_MIN) {
                return deviceName
                        + " memiliki suhu tanah sangat rendah.";
            }

            if (value > SensorThreshold.SUHU_TANAH_PERHATIAN_MAX) {
                return deviceName
                        + " memiliki suhu tanah sangat tinggi.";
            }

            if (value < SensorThreshold.SUHU_TANAH_BAIK_MIN) {
                return deviceName
                        + " memiliki suhu tanah rendah.";
            }

            if (value > SensorThreshold.SUHU_TANAH_BAIK_MAX) {
                return deviceName
                        + " memiliki suhu tanah tinggi.";
            }
        }
        // Nilai masih berada dalam rentang baik
        return null;
    }

    private String determineSeverity(
            String sensorType,
            Double value) {

        if ("PH_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.PH_PERHATIAN_MIN
                    || value > SensorThreshold.PH_PERHATIAN_MAX) {
                return "BAHAYA";
            }

            if (value < SensorThreshold.PH_BAIK_MIN
                    || value > SensorThreshold.PH_BAIK_MAX) {
                return "PERHATIAN";
            }
        }

        if ("SUHU_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.SUHU_TANAH_PERHATIAN_MIN
                    || value > SensorThreshold.SUHU_TANAH_PERHATIAN_MAX) {
                return "BAHAYA";
            }

            if (value < SensorThreshold.SUHU_TANAH_BAIK_MIN
                    || value > SensorThreshold.SUHU_TANAH_BAIK_MAX) {
                return "PERHATIAN";
            }
        }

        if ("SUHU_UDARA".equals(sensorType)) {

            if (value < SensorThreshold.SUHU_UDARA_PERHATIAN_MIN
                    || value > SensorThreshold.SUHU_UDARA_PERHATIAN_MAX) {
                return "BAHAYA";
            }

            if (value < SensorThreshold.SUHU_UDARA_BAIK_MIN
                    || value > SensorThreshold.SUHU_UDARA_BAIK_MAX) {
                return "PERHATIAN";
            }
        }

        if ("KELEMBAPAN_TANAH".equals(sensorType)) {

            if (value < SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MIN
                    || value > SensorThreshold.KELEMBAPAN_TANAH_PERHATIAN_MAX) {
                return "BAHAYA";
            }

            if (value < SensorThreshold.KELEMBAPAN_TANAH_BAIK_MIN
                    || value > SensorThreshold.KELEMBAPAN_TANAH_BAIK_MAX) {
                return "PERHATIAN";
            }
        }

        if ("KELEMBAPAN_UDARA".equals(sensorType)) {

            if (value < SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MIN
                    || value > SensorThreshold.KELEMBAPAN_UDARA_PERHATIAN_MAX) {
                return "BAHAYA";
            }

            if (value < SensorThreshold.KELEMBAPAN_UDARA_BAIK_MIN
                    || value > SensorThreshold.KELEMBAPAN_UDARA_BAIK_MAX) {
                return "PERHATIAN";
            }
        }

        return "BAIK";
    }

    private String getNotificationTitle(
            String sensorType) {

        switch (sensorType) {

            case "SUHU_TANAH":
                return "Suhu Tanah";

            case "SUHU_UDARA":
                return "Suhu Udara";

            case "KELEMBAPAN_TANAH":
                return "Kelembapan Tanah";

            case "KELEMBAPAN_UDARA":
                return "Kelembapan Udara";

            case "PH_TANAH":
                return "pH Tanah";

            default:
                return "Peringatan Sensor";
        }
    }

    public void processSensorStatus(SensorStatusEvent event) {

        DeviceResponse device = restTemplate.getForObject(
                DEVICE_SERVICE_URL + event.getDeviceId(),
                DeviceResponse.class);

        if (device == null) {
            return;
        }

        Notification notification = new Notification();

        notification.setUserId(device.getUserId());
        notification.setDeviceId(event.getDeviceId());

        if (event.isSensorOK()) {

            notification.setSeverity("BAIK");
            notification.setTitle("Sensor Kembali Normal");

            notification.setMessage(
                    "✅ Sensor Normal\n\n" +
                            "Perangkat : " + device.getKodePerangkat() + "\n" +
                            "Sensor : " + getSensorName(event.getSensorType()) + "\n\n" +
                            "Status sensor telah kembali normal dan monitoring dapat dilakukan kembali.");

        } else {

            notification.setSeverity("BAHAYA");
            notification.setTitle("Sensor Bermasalah");

            notification.setMessage(
                    "⚠️ Peringatan Sensor\n\n" +
                            "Perangkat : " + device.getKodePerangkat() + "\n" +
                            "Sensor : " + getSensorName(event.getSensorType()) + "\n\n" +
                            "Status : Tidak berfungsi.\n\n" +
                            "Silakan periksa koneksi kabel atau kondisi sensor agar proses monitoring tetap berjalan.");
        }

        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Simpan ke database
        notificationRepository.save(notification);

        // Kirim ke WhatsApp
        sendWhatsAppNotification(device, notification);
    }

    private String getSensorName(String sensorType) {

        return switch (sensorType) {

            case "SUHU_TANAH" -> "Suhu Tanah";
            case "SUHU_UDARA" -> "Suhu Udara";
            case "PH_TANAH" -> "pH Tanah";
            case "KELEMBAPAN_TANAH" -> "Kelembapan Tanah";
            case "KELEMBAPAN_UDARA" -> "Kelembapan Udara";

            default -> sensorType;
        };
    }

    private void sendWhatsAppNotification(
            DeviceResponse device,
            Notification notification) {

        String phoneNumber = device.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.isBlank()) {
            System.out.println("Nomor WhatsApp user tidak tersedia.");
            return;
        }

        String message = "*" + notification.getTitle() + "*\n\n"
                + notification.getMessage();

        whatsappService.send(phoneNumber, message);
    }

    public void handleDeviceOffline(DeviceOfflineEvent event) {

        Notification notification = new Notification();

        notification.setUserId(event.getUserId());
        notification.setDeviceId(event.getDeviceId());

        notification.setTitle("Perangkat Offline");
        notification.setSeverity("BAHAYA");

        notification.setMessage(
                "Perangkat "
                        + event.getNama()
                        + " (" + event.getKodePerangkat() + ")"
                        + " di lokasi "
                        + event.getLokasi()
                        + " tidak mengirim data sesuai interval yang ditentukan. "
                        + "Silakan periksa catu daya, jaringan, atau kondisi perangkat.");

        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        DeviceResponse device = restTemplate.getForObject(
                DEVICE_SERVICE_URL + event.getDeviceId(),
                DeviceResponse.class);

        if (device != null) {
            sendWhatsAppNotification(device, notification);
        }

    }

    public void handleDeviceOnline(DeviceOnlineEvent event) {

        Notification notification = new Notification();

        notification.setUserId(event.getUserId());
        notification.setDeviceId(event.getDeviceId());

        notification.setTitle("Perangkat Online");
        notification.setSeverity("INFO");

        notification.setMessage(
                "Perangkat "
                        + event.getNama()
                        + " (" + event.getKodePerangkat() + ")"
                        + " di lokasi "
                        + event.getLokasi()
                        + " kembali terhubung dan melanjutkan pengiriman data.");

        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        // Tidak kirim WhatsApp
    }
}