package com.pbl.kelembapantanah.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.kelembapantanah.entity.KelembapanTanah;

public interface KelembapanTanahRepository
                extends JpaRepository<KelembapanTanah, Long> {

        List<KelembapanTanah> findByDeviceId(Long deviceId);

        List<KelembapanTanah> findByDeviceIdAndWaktuBetween(
                        Long deviceId,
                        LocalDateTime start,
                        LocalDateTime end);

        KelembapanTanah findTopByDeviceIdOrderByWaktuDesc(Long deviceId);

}