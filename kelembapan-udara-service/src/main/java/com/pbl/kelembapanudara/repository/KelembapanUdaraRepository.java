package com.pbl.kelembapanudara.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.kelembapanudara.entity.KelembapanUdara;

public interface KelembapanUdaraRepository
        extends JpaRepository<KelembapanUdara, Long> {

    List<KelembapanUdara> findByDeviceId(Long deviceId);

    List<KelembapanUdara> findByDeviceIdAndWaktuBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end);

    KelembapanUdara findTopByDeviceIdOrderByWaktuDesc(Long deviceId);
}