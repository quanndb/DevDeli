package com.example.identityService.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WriteLogRequestDTO {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String actionName;
    String note;
}
