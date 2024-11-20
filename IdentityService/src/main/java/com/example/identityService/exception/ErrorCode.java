package com.example.identityService.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(501, "Invalid key", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_EXCEPTION(403,"You're unable to do this", HttpStatus.FORBIDDEN),

    //
    NOTFOUND_EMAIL(404, "Cannot found your email", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_PASSWORD(405, "Your email or password is invalid", HttpStatus.BAD_REQUEST),
    USER_EXISTED(406, "This email has been created", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(407, "Invalid email has been requested", HttpStatus.BAD_REQUEST),
    EMAIL_PASSWORD_NOT_BLANK(407, "Email and password cannot be blank", HttpStatus.BAD_REQUEST),
    FIELD_NOT_BLANK(408, "These field cannot be blank", HttpStatus.BAD_REQUEST),

    //
    ROLE_NOTFOUND(409, "Cannot found this role", HttpStatus.BAD_REQUEST),
    PERMISSION_NOTFOUND(410, "Cannot found this permission", HttpStatus.BAD_REQUEST),
    ROLE_PERMISSION_NOTFOUND(411, "Cannot found this role-permission", HttpStatus.BAD_REQUEST),
    ;


    int code;
    String message;
    HttpStatusCode statusCode;
}
