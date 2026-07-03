package com.pbl.device.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbl.device.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByKodePerangkat(String kodePerangkat);

    List<Device> findByUserId(Long userId);
}