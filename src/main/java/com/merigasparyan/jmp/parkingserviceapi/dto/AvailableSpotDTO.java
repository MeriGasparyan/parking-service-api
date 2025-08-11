package com.merigasparyan.jmp.parkingserviceapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSpotDTO {
    private Long spotId;
    private String code;
    private SpotType spotType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant availableFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant availableTo;
}
