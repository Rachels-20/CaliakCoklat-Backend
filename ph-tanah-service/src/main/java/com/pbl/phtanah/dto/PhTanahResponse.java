package com.pbl.phtanah.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhTanahResponse {

    private Long id;
    private Long deviceId;
    private Double nilai;
    private LocalDateTime waktu;
}