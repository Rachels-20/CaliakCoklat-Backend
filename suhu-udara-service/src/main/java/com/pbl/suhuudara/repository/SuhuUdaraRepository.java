package com.pbl.suhuudara.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.suhuudara.entity.SuhuUdara;

public interface SuhuUdaraRepository extends JpaRepository<SuhuUdara, Long> {

    List<SuhuUdara> findByDeviceId(Long deviceId);

    List<SuhuUdara> findByDeviceIdAndWaktuBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end);

    SuhuUdara findTopByDeviceIdOrderByWaktuDesc(Long deviceId);
}