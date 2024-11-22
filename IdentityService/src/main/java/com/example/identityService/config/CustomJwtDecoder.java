package com.example.identityService.config;

import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.service.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomJwtDecoder implements JwtDecoder {

    TokenService tokenService;

    @Override
    public Jwt decode(String token){
        boolean isValidToken = tokenService.verifyToken(token);
        var scopeClaim = tokenService.extractClaims(token).get("scope");
        if(!isValidToken || scopeClaim == null) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        return tokenService.getTokenDecoded(token);
    }
}
