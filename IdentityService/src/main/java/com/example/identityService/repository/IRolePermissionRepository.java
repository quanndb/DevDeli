package com.example.identityService.repository;

import com.example.identityService.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRolePermissionRepository extends JpaRepository<RolePermission, String> {
    Optional<RolePermission> findByRoleIdAndPermissionId(String roleId, String permissionId);

    @Query(value = "SELECT * FROM get_role_permissions(:roleIdInput)", nativeQuery = true)
    List<String> getRolePermissions(@Param("roleIdInput") String roleIdInput);
}
