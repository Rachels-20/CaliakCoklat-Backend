package com.pbl.kelembapantanah.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "data_kelembapan_tanah")
public class KelembapanTanah {

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