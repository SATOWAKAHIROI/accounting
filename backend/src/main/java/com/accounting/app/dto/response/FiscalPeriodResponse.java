package com.accounting.app.dto.response;

import com.accounting.app.entity.FiscalPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FiscalPeriodResponse {
    private Long id;
    private Long companyId;
    private Integer periodYear;
    private Integer periodNumber;
    private String periodName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isClosed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FiscalPeriodResponse from(FiscalPeriod period) {
        FiscalPeriodResponse response = new FiscalPeriodResponse();
        response.setId(period.getId());
        response.setCompanyId(period.getCompany().getId());
        response.setPeriodYear(period.getPeriodYear());
        response.setPeriodNumber(period.getPeriodNumber());
        response.setPeriodName(period.getPeriodName());
        response.setStartDate(period.getStartDate());
        response.setEndDate(period.getEndDate());
        response.setIsClosed(period.getIsClosed());
        response.setCreatedAt(period.getCreatedAt());
        response.setUpdatedAt(period.getUpdatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Integer getPeriodYear() { return periodYear; }
    public void setPeriodYear(Integer periodYear) { this.periodYear = periodYear; }

    public Integer getPeriodNumber() { return periodNumber; }
    public void setPeriodNumber(Integer periodNumber) { this.periodNumber = periodNumber; }

    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
