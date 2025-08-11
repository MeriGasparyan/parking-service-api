package com.merigasparyan.jmp.parkingserviceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UpdateCommunityDTO {
    private String name;
    private String address;
    private Long managerId;
}
