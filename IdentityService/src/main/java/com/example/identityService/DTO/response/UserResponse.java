package com.example.identityService.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String fullname;
    private String address;
    private Integer gender;
    private String cloudImageUrl;
}
