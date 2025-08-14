package com.merigasparyan.jmp.parkingserviceapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String firstname;
    private String lastname;

    @Email(message = "Email must be valid")
    private String email;

    private String currentPassword;

    @Size(min = 4, message = "New password must be at least 4 characters long")
    private String newPassword;

    private String role;
}
