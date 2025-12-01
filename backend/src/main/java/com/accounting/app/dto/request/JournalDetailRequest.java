package com.accounting.app.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 仕訳明細リクエストDTO
 */
public class JournalDetailRequest {

    @NotNull(message = "行番号は必須です")
    @Positive(message = "行番号は正の整数である必要があります")
    private Integer lineNumber;

    @NotNull(message = "借方/貸方区分は必須です")
    private String entryType; // DEBIT, CREDIT

    @NotNull(message = "勘定科目IDは必須です")
    private Long accountId;

    private Long subAccountId;

    private Long taxTypeId;

    private Long partnerId;

    @NotNull(message = "金額は必須です")
    @Positive(message = "金額は正の数である必要があります")
    private BigDecimal amount;

    private BigDecimal taxAmount;

    @Size(max = 500, message = "摘要は500文字以内で入力してください")
    private String description;

    // Getters and Setters
    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSubAccountId() {
        return subAccountId;
    }

    public void setSubAccountId(Long subAccountId) {
        this.subAccountId = subAccountId;
    }

    public Long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
