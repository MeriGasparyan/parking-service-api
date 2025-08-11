package com.merigasparyan.jmp.parkingserviceapi.dto;

import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSpotDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String address;
    @NotBlank
    private SpotType spotType;
    @NotBlank
    private Long communityId;
}
