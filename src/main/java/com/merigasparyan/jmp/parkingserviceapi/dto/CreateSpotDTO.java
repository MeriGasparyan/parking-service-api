package com.merigasparyan.jmp.parkingserviceapi.dto;

import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSpotDTO {
    @NotBlank(message = "Spot code is required")
    private String code;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Spot type is required")
    private SpotType spotType;
}
