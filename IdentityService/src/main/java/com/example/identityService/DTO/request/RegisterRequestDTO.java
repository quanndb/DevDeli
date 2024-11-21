package com.example.identityService.DTO.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequestDTO {
    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_PASSWORD_NOT_BLANK")
    String email;
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_AT_LEAST")
    String password;
    @NotBlank(message = "FIELD_NOT_BLANK")
    String fullname;
    Integer gender;
    String address;
}
