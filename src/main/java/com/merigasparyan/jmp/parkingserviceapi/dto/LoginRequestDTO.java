package com.merigasparyan.jmp.parkingserviceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;
}