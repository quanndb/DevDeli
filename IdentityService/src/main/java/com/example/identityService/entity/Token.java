package com.example.identityService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Token {
    String value;
    Integer lifeTime;
}
