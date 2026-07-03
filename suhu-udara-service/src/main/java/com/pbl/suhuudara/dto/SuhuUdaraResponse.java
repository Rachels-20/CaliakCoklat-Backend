package com.pbl.suhuudara.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuhuUdaraResponse {

    private Long id;
    private Long deviceId;
    private Double nilai;
    private LocalDateTime waktu;
}