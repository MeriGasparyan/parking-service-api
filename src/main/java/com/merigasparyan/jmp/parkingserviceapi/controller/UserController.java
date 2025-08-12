package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UpdateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UserDTO;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<UserDTO> createUser(
            @RequestBody CreateUserDTO dto) {

        return new ResponseEntity<>(userService.createAdmin(dto), HttpStatus.CREATED);
    }

    @PostMapping("/community/{communityId}")
    public ResponseEntity<UserDTO> createUser(@PathVariable Long communityId,
                                              @RequestBody CreateUserDTO dto) {

        return new ResponseEntity<>(userService.createUser(dto, communityId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UpdateUserDTO dto,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(userService.updateUser(id, currentUser.getId(), dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        userService.deleteUser(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }


}