package com.accounting.app.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class InvoiceDetailRequest {
    @NotNull @Positive
    private Integer lineNumber;
    private Long itemId;
    @NotBlank @Size(max = 255)
    private String description;
    @NotNull @Positive
    private BigDecimal quantity;
    @NotNull @Positive
    private BigDecimal unitPrice;
    private Long taxTypeId;

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
}
