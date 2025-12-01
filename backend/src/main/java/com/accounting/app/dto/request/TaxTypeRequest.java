package com.accounting.app.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxTypeRequest {

    @NotBlank(message = "コードは必須です")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "名前は必須です")
    @Size(max = 100)
    private String name;

    @NotNull(message = "税率は必須です")
    @DecimalMin(value = "0.0", message = "税率は0以上である必要があります")
    @DecimalMax(value = "100.0", message = "税率は100以下である必要があります")
    private BigDecimal taxRate;

    @NotNull(message = "有効開始日は必須です")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

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
}
