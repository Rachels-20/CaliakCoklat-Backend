package com.pbl.suhutanah.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.suhutanah.entity.SuhuTanah;

public interface SuhuTanahRepository extends JpaRepository<SuhuTanah, Long> {

    List<SuhuTanah> findByDeviceId(Long deviceId);

    List<SuhuTanah> findByDeviceIdAndWaktuBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end);

    SuhuTanah findTopByDeviceIdOrderByWaktuDesc(Long deviceId);
}