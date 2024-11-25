package com.example.identityService.DTO.request;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String fullname;
    private String address;
    private Integer gender;
    private String cloudImageUrl;
}
