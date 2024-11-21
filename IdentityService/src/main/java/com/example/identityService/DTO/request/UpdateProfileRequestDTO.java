package com.example.identityService.DTO.request;

import lombok.Getter;

@Getter
public class UpdateProfileRequestDTO {
    String fullname;
    String address;
    Integer gender;
    String cloudImageUrl;
}
