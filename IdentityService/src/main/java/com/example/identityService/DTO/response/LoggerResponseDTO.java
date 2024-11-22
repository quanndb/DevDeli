package com.example.identityService.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoggerResponseDTO {
    String id;
    String email;
    String ip;
    String actionName;
    String dateTime;
    String note;
}
