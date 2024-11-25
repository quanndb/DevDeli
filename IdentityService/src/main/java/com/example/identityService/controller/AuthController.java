package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.DTO.request.LoginRequest;
import com.example.identityService.DTO.request.LogoutRequest;
import com.example.identityService.DTO.request.RegisterRequest;
import com.example.identityService.DTO.request.ChangePasswordRequest;
import com.example.identityService.DTO.request.UpdateProfileRequest;
import com.example.identityService.DTO.request.GetNewTokenRequest;
import com.example.identityService.DTO.request.ResetPasswordRequest;
import com.example.identityService.DTO.response.LoginResponse;
import com.example.identityService.DTO.response.UserResponse;
import com.example.identityService.Util.IpChecker;
import com.example.identityService.Util.JsonMapper;
import com.example.identityService.Util.ObjectValidator;
import com.example.identityService.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final ObjectValidator objectValidator;
    private final JsonMapper jsonMapper;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest dto, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        var res = authService.login(dto, ip);
        return ApiResponse.<LoginResponse>builder()
                .code(200)
                .result(res)
                .build();
    }

    @PostMapping("/registration")
    public ApiResponse<Boolean> register(@RequestBody @Valid RegisterRequest dto, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("Please check your verification email")
                .result(authService.register(dto, ip))
                .build();
    }

    @GetMapping("/verification")
    public ApiResponse<Object> verifyEmailAndIP(@RequestParam String token, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        return ApiResponse.builder()
                .code(200)
                .result(authService.verifyEmailAndIP(token, ip))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest requestHeader, @RequestBody @Valid LogoutRequest requestBody){
        String accessToken = requestHeader.getHeader("Authorization").substring(7);
        String refreshToken = requestBody.getRefreshToken();
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.logout(accessToken, refreshToken)?"ok":"fail")
                .build();
    }

    @PostMapping("/forgotPasswordProcess")
    public ApiResponse<Boolean> fogotPasswordAttemp(@RequestParam String email, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.forgotPassword(email, ip)?"check your email to reset password":"fail")
                .build();
    }

    @PostMapping("/resetPasswordProcess")
    public ApiResponse<Boolean> resetPassword(@RequestBody @Valid ResetPasswordRequest passwordRequestDTO, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService
                        .resetPassword(passwordRequestDTO.getToken(),
                                passwordRequestDTO.getNewPassword(), ip)?"ok":"fail")
                .build();
    }

    @GetMapping("/token")
    public ApiResponse<String> getNewAccessToken(@RequestBody @Valid GetNewTokenRequest requestBody){
        String refreshToken = requestBody.getRefreshToken();
        return ApiResponse.<String>builder()
                .code(200)
                .result(authService.getNewAccessToken(refreshToken))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getProfile(){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(authService.getProfile())
                .build();
    }

    @PatchMapping("/me")
    public ApiResponse<Boolean> updateProfile(
            @RequestParam(value = "userData", required = false) String userData,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        UpdateProfileRequest updateRequest = null;
        if(userData != null){
            updateRequest = jsonMapper
                    .JSONToObject(userData, UpdateProfileRequest.class);
            objectValidator.validateObject(updateRequest);
        }
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.updateProfile(updateRequest, image)?"ok":"fail")
                .build();
    }

    @PutMapping("/me/password")
    public ApiResponse<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordDTO, HttpServletRequest request){
        String ip = IpChecker.getClientIpFromRequest(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(authService.changePassword(changePasswordDTO, ip)?"ok":"fail")
                .build();
    }
}
