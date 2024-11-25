package com.example.identityService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggerService {
    private final ActionLogService actionLogService;

    @PreAuthorize("hasRole('ADMIN')")
    public Object getLoggers(int page, int size, String query, String sortedBy, String sortDirection){
        return actionLogService.callProcedure(page, size, query, sortedBy, sortDirection);
    }
}
