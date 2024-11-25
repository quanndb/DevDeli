package com.example.identityService.service;

import com.example.identityService.entity.RolePermission;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.repository.IPermissionRepository;
import com.example.identityService.repository.IRolePermissionRepository;
import com.example.identityService.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;

    public boolean assignPermission(String roleId, String permissionId){
        roleRepository.findById(roleId).orElseThrow(()-> new AppExceptions(ErrorCode.ROLE_NOTFOUND));
        permissionRepository.findById(roleId).orElseThrow(()-> new AppExceptions(ErrorCode.PERMISSION_NOTFOUND));
        rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .ifPresent(_-> {throw new AppExceptions(ErrorCode.ROLE_PERMISSION_EXISTED);});
        rolePermissionRepository.save(RolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .createdDate(LocalDate.now())
                        .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .build());
        return true;
    }

    // un assign
    public boolean unAssignPermission(String roleId, String permissionId) {
        RolePermission rolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.ROLE_PERMISSION_NOTFOUND));
        rolePermissionRepository.delete(rolePermission);
        return true;
    }

    public String getAllRolePermission(String roleId){
        return String.join(" ", rolePermissionRepository.getRolePermissions(roleId));
    }
}
