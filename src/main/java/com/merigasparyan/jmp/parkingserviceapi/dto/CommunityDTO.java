package com.merigasparyan.jmp.parkingserviceapi.dto;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDTO {
    private Long id;
    private String name;
    private String address;
    private Long managerId;
    private String managerName;

    public static CommunityDTO mapToDTO(Community community) {
        return new CommunityDTO(
                community.getId(),
                community.getName(),
                community.getAddress(),
                community.getCommunityManager() != null ? community.getCommunityManager().getId() : null,
                community.getCommunityManager() != null ? community.getCommunityManager().getUsername() : null
        );
    }
}
