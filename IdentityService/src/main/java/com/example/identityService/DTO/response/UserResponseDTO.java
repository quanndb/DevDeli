package com.example.identityService.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserResponseDTO {
    String email;
    String fullname;
    String address;
    Integer gender;
    String cloudImageUrl;
}
