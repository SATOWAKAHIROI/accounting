package com.accounting.app.dto.response;

import com.accounting.app.entity.JournalDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JournalDetailResponse {
    private Long id;
    private Integer lineNumber;
    private String entryType;
    private Long accountId;
    private String accountCode;
    private String accountName;
    private Long subAccountId;
    private String subAccountCode;
    private String subAccountName;
    private Long taxTypeId;
    private Long partnerId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JournalDetailResponse from(JournalDetail detail) {
        JournalDetailResponse response = new JournalDetailResponse();
        response.setId(detail.getId());
        response.setLineNumber(detail.getLineNumber());
        response.setEntryType(detail.getEntryType().name());
        response.setAccountId(detail.getAccount().getId());
        response.setAccountCode(detail.getAccount().getCode());
        response.setAccountName(detail.getAccount().getName());
        if (detail.getSubAccount() != null) {
            response.setSubAccountId(detail.getSubAccount().getId());
            response.setSubAccountCode(detail.getSubAccount().getCode());
            response.setSubAccountName(detail.getSubAccount().getName());
        }
        if (detail.getTaxType() != null) {
            response.setTaxTypeId(detail.getTaxType().getId());
        }
        if (detail.getPartner() != null) {
            response.setPartnerId(detail.getPartner().getId());
        }
        response.setAmount(detail.getAmount());
        response.setTaxAmount(detail.getTaxAmount());
        response.setDescription(detail.getDescription());
        response.setCreatedAt(detail.getCreatedAt());
        response.setUpdatedAt(detail.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public Long getSubAccountId() { return subAccountId; }
    public void setSubAccountId(Long subAccountId) { this.subAccountId = subAccountId; }
    public String getSubAccountCode() { return subAccountCode; }
    public void setSubAccountCode(String subAccountCode) { this.subAccountCode = subAccountCode; }
    public String getSubAccountName() { return subAccountName; }
    public void setSubAccountName(String subAccountName) { this.subAccountName = subAccountName; }
    public Long getTaxTypeId() { return taxTypeId; }
    public void setTaxTypeId(Long taxTypeId) { this.taxTypeId = taxTypeId; }
    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
