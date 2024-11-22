package com.example.identityService.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GetNewTokenRequestDTO {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String refreshToken;
}
