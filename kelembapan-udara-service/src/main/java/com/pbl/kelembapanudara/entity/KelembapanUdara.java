package com.pbl.kelembapanudara.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "data_kelembapan_udara")
public class KelembapanUdara {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private Double nilai;

    @Column(nullable = false)
    private boolean sensorOK;

    @Column(nullable = false)
    private LocalDateTime waktu;
}