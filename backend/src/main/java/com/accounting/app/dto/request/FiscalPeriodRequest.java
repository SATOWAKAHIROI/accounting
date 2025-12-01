package com.accounting.app.dto.request;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class FiscalPeriodRequest {
    @NotNull
    private Integer periodYear;
    @NotNull
    private Integer periodNumber;
    @NotBlank @Size(max = 100)
    private String periodName;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

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
}
