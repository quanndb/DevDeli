package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> getProducts(){
        return ApiResponse.<String>builder()
                .message("ok")
                .build();
    }
}
