package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.LoginRequestDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.LoginResponseDTO;
import com.merigasparyan.jmp.parkingserviceapi.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequestDto));
    }

}
