package com.pbl.kelembapantanah.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KelembapanTanahRequest {

    @NotNull
    private String kodePerangkat;

    @NotNull
    private Double nilai;

    @NotNull
    private boolean sensorOK;
}