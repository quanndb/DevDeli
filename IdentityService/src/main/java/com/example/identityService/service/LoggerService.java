package com.example.identityService.service;

import com.example.identityService.DTO.response.LoggerResponseDTO;
import com.example.identityService.entity.Logs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoggerService {
    ActionLogService actionLogService;

    @PreAuthorize("hasRole('ADMIN')")
    public Object getLoggers(int page, int size, String query, String sortedBy, String sortDirection){
        return actionLogService.callProcedure(page, size, query, sortedBy, sortDirection);
    }
}
