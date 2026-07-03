package com.pbl.suhutanah.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuhuTanahRequest {

    @NotNull
    private String kodePerangkat;

    @NotNull
    private Double nilai;

    @NotNull
    private boolean sensorOK;
}