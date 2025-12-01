package com.accounting.app.dto.response;

import com.accounting.app.entity.SubAccount;

import java.time.LocalDateTime;

/**
 * 補助科目レスポンスDTO
 */
public class SubAccountResponse {

    private Long id;
    private String code;
    private String name;
    private Long accountId;
    private String accountCode;
    private String accountName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubAccountResponse() {
    }

    /**
     * Entityから変換
     */
    public static SubAccountResponse from(SubAccount subAccount) {
        SubAccountResponse response = new SubAccountResponse();
        response.setId(subAccount.getId());
        response.setCode(subAccount.getCode());
        response.setName(subAccount.getName());
        response.setAccountId(subAccount.getAccount().getId());
        response.setAccountCode(subAccount.getAccount().getCode());
        response.setAccountName(subAccount.getAccount().getName());
        response.setCreatedAt(subAccount.getCreatedAt());
        response.setUpdatedAt(subAccount.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
