package com.accounting.app.dto.response;

import com.accounting.app.entity.TaxType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaxTypeResponse {
    private Long id;
    private String code;
    private String name;
    private BigDecimal taxRate;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaxTypeResponse from(TaxType taxType) {
        TaxTypeResponse response = new TaxTypeResponse();
        response.setId(taxType.getId());
        response.setCode(taxType.getCode());
        response.setName(taxType.getName());
        response.setTaxRate(taxType.getTaxRate());
        response.setEffectiveFrom(taxType.getEffectiveFrom());
        response.setEffectiveTo(taxType.getEffectiveTo());
        response.setCreatedAt(taxType.getCreatedAt());
        response.setUpdatedAt(taxType.getUpdatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
