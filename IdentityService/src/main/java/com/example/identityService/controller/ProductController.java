package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    @GetMapping
    @PreAuthorize("hasRole('GET_PRODUCT')")
    public ApiResponse<String> getProducts(){
        return ApiResponse.<String>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
