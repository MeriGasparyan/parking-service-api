package com.merigasparyan.jmp.parkingserviceapi.dto;

import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Spot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpotDTO {

    private Long id;
    private String code;
    private String address;
    private SpotType spotType;
    private Long communityId;

    public static SpotDTO mapToSpotDto(Spot spot) {
        return new SpotDTO(
                spot.getId(),
                spot.getCode(),
                spot.getAddress(),
                spot.getSpotType(),
                spot.getCommunity().getId()
        );
    }
}
