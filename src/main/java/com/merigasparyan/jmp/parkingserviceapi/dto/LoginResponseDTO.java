package com.merigasparyan.jmp.parkingserviceapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
public class LoginResponseDTO {

    private String email;
    private String accessToken;
}
