package com.example.identityService.controller;

import com.example.identityService.DTO.ApiResponse;
import com.example.identityService.service.RolePermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RolePermissionService rolePermissionService;

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ApiResponse<String> assignPermissionForRole(@PathVariable String roleId, @PathVariable String permissionId){
        return ApiResponse.<String>builder()
                .code(200)
                .message(rolePermissionService.assignPermission(roleId, permissionId)?"ok":"fail")
                .build();
    }
}
