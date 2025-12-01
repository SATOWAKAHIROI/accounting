package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.AccountRequest;
import com.accounting.app.dto.response.AccountResponse;
import com.accounting.app.entity.Account;
import com.accounting.app.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 勘定科目管理API
 */
@RestController
@RequestMapping("/companies/{companyId}/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 勘定科目一覧取得
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> findAll(
            @PathVariable Long companyId,
            @RequestParam(required = false) String accountType) {

        List<AccountResponse> accounts;
        if (accountType != null && !accountType.isEmpty()) {
            Account.AccountType type = Account.AccountType.valueOf(accountType);
            accounts = accountService.findByAccountType(companyId, type);
        } else {
            accounts = accountService.findAll(companyId);
        }

        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    /**
     * 勘定科目詳細取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {

        AccountResponse account = accountService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    /**
     * 勘定科目作成
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody AccountRequest request) {

        AccountResponse account = accountService.create(companyId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(account));
    }

    /**
     * 勘定科目更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request) {

        AccountResponse account = accountService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    /**
     * 勘定科目削除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {

        accountService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null));
    }
}
