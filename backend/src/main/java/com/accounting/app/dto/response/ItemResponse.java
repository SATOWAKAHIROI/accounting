package com.accounting.app.dto.response;

import com.accounting.app.entity.Item;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setCode(item.getCode());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setUnitPrice(item.getUnitPrice());
        response.setUnit(item.getUnit());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
