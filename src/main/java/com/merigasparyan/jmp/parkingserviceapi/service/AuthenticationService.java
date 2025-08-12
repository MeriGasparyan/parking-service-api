package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.LoginRequestDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.LoginResponseDTO;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        final String accessToken = jwtUtil.generateAccessToken(userDetails);

        return LoginResponseDTO.builder()
                .withEmail(userDetails.getUsername())
                .withAccessToken(accessToken)
                .build();
    }
}
