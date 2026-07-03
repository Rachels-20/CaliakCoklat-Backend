package com.pbl.kelembapanudara.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KelembapanUdaraRequest {

    @NotNull
    private String kodePerangkat;
    @NotNull
    private Double nilai;
    @NotNull
    private boolean sensorOK;
}
