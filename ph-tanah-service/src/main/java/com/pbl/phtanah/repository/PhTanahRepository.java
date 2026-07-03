package com.pbl.phtanah.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.phtanah.entity.PhTanah;

public interface PhTanahRepository extends JpaRepository<PhTanah, Long> {

    List<PhTanah> findByDeviceId(Long deviceId);

    List<PhTanah> findByDeviceIdAndWaktuBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end);

    PhTanah findTopByDeviceIdOrderByWaktuDesc(Long deviceId);
}