package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.DTO.request.LoginRequestDTO;
import com.example.identityService.DTO.request.RegisterRequestDTO;
import com.example.identityService.DTO.response.LoginResponseDTO;
import com.example.identityService.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto){
        var res = authService.login(dto);
        return ApiResponse.<LoginResponseDTO>builder()
                .code(200)
                .result(res)
                .build();
    }

    @PostMapping("/registration")
    public ApiResponse<Boolean> register(@RequestBody @Valid RegisterRequestDTO dto){
        return ApiResponse.<Boolean>builder()
                .code(200)
                .result(authService.register(dto))
                .build();
    }
}
