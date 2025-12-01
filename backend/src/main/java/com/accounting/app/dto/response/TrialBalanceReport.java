package com.accounting.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 試算表レポート
 */
public class TrialBalanceReport {
    private LocalDate asOfDate;
    private List<TrialBalanceEntry> entries = new ArrayList<>();
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal difference;

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }

    public List<TrialBalanceEntry> getEntries() { return entries; }
    public void setEntries(List<TrialBalanceEntry> entries) { this.entries = entries; }

    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }

    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }

    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }
}
