package com.pbl.suhutanah.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "data_suhu_tanah")
public class SuhuTanah {

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