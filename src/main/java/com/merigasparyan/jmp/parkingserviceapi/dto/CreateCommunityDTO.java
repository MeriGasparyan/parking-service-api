package com.merigasparyan.jmp.parkingserviceapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CreateCommunityDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    private Long managerId;
}
