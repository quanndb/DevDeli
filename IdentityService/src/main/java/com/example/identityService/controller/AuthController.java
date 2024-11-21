package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.DTO.request.ChangePasswordRequestDTO;
import com.example.identityService.DTO.request.LoginRequestDTO;
import com.example.identityService.DTO.request.RegisterRequestDTO;
import com.example.identityService.DTO.request.UpdateProfileRequestDTO;
import com.example.identityService.DTO.response.LoginResponseDTO;
import com.example.identityService.DTO.response.UserResponseDTO;
import com.example.identityService.Util.JsonMapper;
import com.example.identityService.Util.ObjectValidator;
import com.example.identityService.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    ObjectValidator objectValidator;
    JsonMapper jsonMapper;

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

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.logout(token)?"ok":"fail")
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> getProfile(){
        return ApiResponse.<UserResponseDTO>builder()
                .code(200)
                .result(authService.getProfile())
                .build();
    }

    @PatchMapping("/me")
    public ApiResponse<Boolean> updateProfile(
            @RequestParam(value = "userData", required = false) String userData,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        UpdateProfileRequestDTO updateRequest = null;
        if(userData != null){
            updateRequest = jsonMapper
                    .JSONToObject(userData, UpdateProfileRequestDTO.class);
            objectValidator.validateObject(updateRequest);
        }
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.updateProfile(updateRequest, image)?"ok":"fail")
                .build();
    }

    @PutMapping("/me/password")
    public ApiResponse<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequestDTO request){
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.changePassword(request)?"ok":"fail")
                .build();
    }
}
