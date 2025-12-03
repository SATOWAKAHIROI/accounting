package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.SubAccountRequest;
import com.accounting.app.dto.response.SubAccountResponse;
import com.accounting.app.security.JwtTokenProvider.UserPrincipal;
import com.accounting.app.service.CompanyAccessService;
import com.accounting.app.service.SubAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/sub-accounts")
public class SubAccountController {

    private final SubAccountService subAccountService;
    private final CompanyAccessService companyAccessService;

    public SubAccountController(SubAccountService subAccountService,
                               CompanyAccessService companyAccessService) {
        this.subAccountService = subAccountService;
        this.companyAccessService = companyAccessService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubAccountResponse>>> findAll(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        companyAccessService.validateAccess(currentUser.getUserId(), companyId);
        List<SubAccountResponse> subAccounts = subAccountService.findAll(companyId);
        return ResponseEntity.ok(ApiResponse.success(subAccounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubAccountResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        companyAccessService.validateAccess(currentUser.getUserId(), companyId);
        SubAccountResponse subAccount = subAccountService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(subAccount));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubAccountResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody SubAccountRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        companyAccessService.validateAccess(currentUser.getUserId(), companyId);
        SubAccountResponse subAccount = subAccountService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(subAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubAccountResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody SubAccountRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        companyAccessService.validateAccess(currentUser.getUserId(), companyId);
        SubAccountResponse subAccount = subAccountService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(subAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        companyAccessService.validateAccess(currentUser.getUserId(), companyId);
        subAccountService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
