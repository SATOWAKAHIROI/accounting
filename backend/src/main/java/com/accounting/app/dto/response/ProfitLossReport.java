package com.accounting.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 損益計算書レポート
 */
public class ProfitLossReport {
    private LocalDate startDate;
    private LocalDate endDate;

    // 売上
    private BigDecimal revenue = BigDecimal.ZERO;

    // 費用
    private BigDecimal expense = BigDecimal.ZERO;

    // 純利益
    private BigDecimal netProfit = BigDecimal.ZERO;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }

    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
}
