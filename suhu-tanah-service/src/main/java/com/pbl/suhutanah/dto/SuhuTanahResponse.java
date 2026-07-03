package com.pbl.suhutanah.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuhuTanahResponse {

    private Long id;
    private Long deviceId;
    private Double nilai;
    private LocalDateTime waktu;
}