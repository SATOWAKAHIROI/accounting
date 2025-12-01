package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.LoginRequest;
import com.accounting.app.dto.request.UserRequest;
import com.accounting.app.dto.response.AuthResponse;
import com.accounting.app.dto.response.UserResponse;
import com.accounting.app.entity.UserCompanyRole;
import com.accounting.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 認証
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = userService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(auth));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        UserResponse user = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    /**
     * ロール割り当て
     */
    @PostMapping("/{userId}/companies/{companyId}/roles")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable Long userId,
            @PathVariable Long companyId,
            @RequestParam String role) {
        UserCompanyRole.RoleType roleType = UserCompanyRole.RoleType.valueOf(role);
        userService.assignRole(userId, companyId, roleType);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
