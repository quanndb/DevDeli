package com.example.identityService.DTO.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequestDTO {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String token;
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    String newPassword;
}
