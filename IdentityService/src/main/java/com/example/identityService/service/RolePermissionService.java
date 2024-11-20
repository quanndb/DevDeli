package com.example.identityService.service;

import com.example.identityService.entity.RolePermission;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.repository.IPermissionRepository;
import com.example.identityService.repository.IRolePermissionRepository;
import com.example.identityService.repository.IRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RolePermissionService {
    IRoleRepository roleRepository;
    IPermissionRepository permissionRepository;
    IRolePermissionRepository rolePermissionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public boolean assignPermission(String roleId, String permissionId){
        roleRepository.findById(roleId).orElseThrow(()-> new AppExceptions(ErrorCode.ROLE_NOTFOUND));
        permissionRepository.findById(roleId).orElseThrow(()-> new AppExceptions(ErrorCode.PERMISSION_NOTFOUND));
        rolePermissionRepository.save(RolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .createdDate(LocalDateTime.now())
                        .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .build());
        return true;
    }

    // un assign
    @PreAuthorize("hasRole('ADMIN')")
    public boolean unAssignPermission(String rolePermissionId) {
        RolePermission rolePermission = rolePermissionRepository.findById(rolePermissionId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.ROLE_PERMISSION_NOTFOUND));
        rolePermissionRepository.delete(rolePermission);
        return true;
    }
}
