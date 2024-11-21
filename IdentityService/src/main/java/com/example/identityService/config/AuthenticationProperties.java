package com.example.identityService.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "security.authentication.jwt")
public class AuthenticationProperties {
    String keyStore;
    String keyStorePassword;
    String keyAlias;
}
