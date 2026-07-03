package com.pbl.suhuudara.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuhuUdaraRequest {

    @NotNull
    private String kodePerangkat;

    @NotNull
    private Double nilai;

    @NotNull
    private boolean sensorOK;
}