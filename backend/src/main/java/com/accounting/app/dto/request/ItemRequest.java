package com.accounting.app.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class ItemRequest {
    @NotBlank(message = "コードは必須です")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "名前は必須です")
    @Size(max = 255)
    private String name;

    private String description;

    @DecimalMin(value = "0.0", message = "単価は0以上である必要があります")
    private BigDecimal unitPrice;

    @Size(max = 50)
    private String unit;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
