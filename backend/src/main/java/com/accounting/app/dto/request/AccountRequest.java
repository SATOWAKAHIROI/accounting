package com.accounting.app.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 勘定科目リクエストDTO
 */
public class AccountRequest {

    @NotBlank(message = "コードは必須です")
    @Size(max = 50, message = "コードは50文字以内で入力してください")
    private String code;

    @NotBlank(message = "名前は必須です")
    @Size(max = 100, message = "名前は100文字以内で入力してください")
    private String name;

    @NotNull(message = "勘定科目タイプは必須です")
    private String accountType; // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE

    private Boolean isSystem = false;

    // Getters and Setters
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
}
