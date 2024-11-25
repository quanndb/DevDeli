package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.service.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LoggerController {

    private final LoggerService loggerService;

    @GetMapping
    public ApiResponse<Object> getLogs(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "id") String sortedBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection){
        return ApiResponse.<Object>builder()
                .code(200)
                .result(loggerService.getLoggers(page,size,query,sortedBy,sortDirection))
                .build();
    }
}
