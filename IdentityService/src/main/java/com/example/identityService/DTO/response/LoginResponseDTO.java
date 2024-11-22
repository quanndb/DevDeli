package com.example.identityService.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDTO {
    String accessToken;
    String refreshToken;
    String email;
    String fullname;
    String cloudImageUrl;
}
