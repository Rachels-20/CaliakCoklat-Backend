package com.pbl.device.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pbl.device.config.RabbitMQConfig;
import com.pbl.device.dto.DeviceOfflineEvent;
import com.pbl.device.dto.DeviceOnlineEvent;
import com.pbl.device.entity.Device;
import com.pbl.device.repository.DeviceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceStatusScheduler {

    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 60000)
    public void checkDeviceStatus() {

        List<Device> devices = deviceRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Device device : devices) {

            if (device.getLastSeen() == null) {
                continue;
            }

            long intervalMs = device.getIntervalPengiriman();

            long timeoutMs = intervalMs + (intervalMs / 2);

            LocalDateTime batasOffline = device.getLastSeen()
                    .plusNanos(timeoutMs * 1_000_000);

            boolean offline = now.isAfter(batasOffline);

            // Perangkat baru saja OFFLINE
            if (offline && device.getAktif()) {

                device.setAktif(false);

                deviceRepository.save(device);

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE,
                        RabbitMQConfig.DEVICE_OFFLINE,
                        new DeviceOfflineEvent(
                                device.getId(),
                                device.getUserId(),
                                device.getKodePerangkat(),
                                device.getNama(),
                                device.getLokasi()));

                System.out.println("[OFFLINE] "
                        + device.getKodePerangkat());

            }

            // Perangkat baru saja ONLINE
            else if (!offline && !device.getAktif()) {

                device.setAktif(true);

                deviceRepository.save(device);

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE,
                        RabbitMQConfig.DEVICE_ONLINE,
                        new DeviceOnlineEvent(
                                device.getId(),
                                device.getUserId(),
                                device.getKodePerangkat(),
                                device.getNama(),
                                device.getLokasi()));

                System.out.println(
                        "[ONLINE] "
                                + device.getKodePerangkat());
            }

            System.out.println("=======================");
            System.out.println("Kode      : " + device.getKodePerangkat());
            System.out.println("Last Seen : " + device.getLastSeen());
            System.out.println("Now       : " + now);
            System.out.println("Offline   : " + offline);
            System.out.println("Aktif DB  : " + device.getAktif());
            System.out.println("=======================");
        }
    }
}