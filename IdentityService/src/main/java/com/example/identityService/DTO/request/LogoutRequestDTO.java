package com.example.identityService.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequestDTO {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String refreshToken;
}
