package com.tricol.Tricol.controller;

import com.tricol.Tricol.dto.request.AssignRoleRequest;
import com.tricol.Tricol.dto.request.PermissionOverrideRequest;
import com.tricol.Tricol.enums.AuditAction;
import com.tricol.Tricol.enums.AuditResourceType;
import com.tricol.Tricol.model.Permission;
import com.tricol.Tricol.model.RoleApp;
import com.tricol.Tricol.model.UserApp;
import com.tricol.Tricol.model.UserPermission;
import com.tricol.Tricol.repository.PermissionRepository;
import com.tricol.Tricol.repository.RoleRepository;
import com.tricol.Tricol.repository.UserPermissionRepository;
import com.tricol.Tricol.repository.UserRepository;
import com.tricol.Tricol.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_USERS')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final AuditService auditService;

    @PostMapping("/users/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody AssignRoleRequest request) {
        UserApp user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleApp role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        userRepository.save(user);

        auditService.logSuccess(AuditAction.ROLE_ASSIGNED, AuditResourceType.USER, user.getId());

        return ResponseEntity.ok("Role assigned successfully");
    }

    @PostMapping("/users/permission-override")
    public ResponseEntity<String> overridePermission(@RequestBody PermissionOverrideRequest request) {
        UserApp user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Permission permission = permissionRepository.findByName(request.getPermissionName())
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserApp admin = userRepository.findByEmail(authentication.getName()).orElse(null);

        UserPermission userPermission = UserPermission.builder()
                .user(user)
                .permission(permission)
                .granted(request.getGranted())
                .grantedBy(admin)
                .build();

        userPermissionRepository.save(userPermission);

        AuditAction action = request.getGranted() ? AuditAction.PERMISSION_GRANTED : AuditAction.PERMISSION_REVOKED;
        auditService.logSuccess(action, AuditResourceType.USER_PERMISSION, user.getId());

        return ResponseEntity.ok("Permission override applied successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getAllPermissions() {
        return ResponseEntity.ok(permissionRepository.findAll());
    }
}
