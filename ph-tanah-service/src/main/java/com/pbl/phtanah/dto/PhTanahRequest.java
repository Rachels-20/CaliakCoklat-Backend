package com.pbl.phtanah.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PhTanahRequest {

    @NotNull
    private String kodePerangkat;

    @NotNull
    private Double nilai;

    @NotNull
    private boolean sensorOK;

}