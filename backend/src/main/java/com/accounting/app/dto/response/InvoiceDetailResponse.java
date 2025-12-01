package com.accounting.app.dto.response;

import com.accounting.app.entity.InvoiceDetail;
import java.math.BigDecimal;

public class InvoiceDetailResponse {
    private Long id;
    private Integer lineNumber;
    private Long itemId;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private Long taxTypeId;
    private BigDecimal taxAmount;
    private BigDecimal amount;

    public static InvoiceDetailResponse from(InvoiceDetail detail) {
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        response.setId(detail.getId());
        response.setLineNumber(detail.getLineNumber());
        if (detail.getItem() != null) response.setItemId(detail.getItem().getId());
        response.setDescription(detail.getDescription());
        response.setQuantity(detail.getQuantity());
        response.setUnitPrice(detail.getUnitPrice());
        if (detail.getTaxType() != null) response.setTaxTypeId(detail.getTaxType().getId());
        response.setTaxAmount(detail.getTaxAmount());
        response.setAmount(detail.getAmount());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Long getTaxTypeId() { return taxTypeId; }
    public void setTaxTypeId(Long taxTypeId) { this.taxTypeId = taxTypeId; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
