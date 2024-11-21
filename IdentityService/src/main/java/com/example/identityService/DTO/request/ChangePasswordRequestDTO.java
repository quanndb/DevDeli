package com.example.identityService.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequestDTO {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String currentPassword;
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    String newPassword;
}
