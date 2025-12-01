package com.accounting.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 貸借対照表レポート
 */
public class BalanceSheetReport {
    private LocalDate asOfDate;

    // 資産
    private BigDecimal assets = BigDecimal.ZERO;

    // 負債
    private BigDecimal liabilities = BigDecimal.ZERO;

    // 純資産
    private BigDecimal equity = BigDecimal.ZERO;

    // 当期純利益
    private BigDecimal netProfit = BigDecimal.ZERO;

    // 負債・純資産合計
    private BigDecimal totalLiabilitiesAndEquity = BigDecimal.ZERO;

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }

    public BigDecimal getAssets() { return assets; }
    public void setAssets(BigDecimal assets) { this.assets = assets; }

    public BigDecimal getLiabilities() { return liabilities; }
    public void setLiabilities(BigDecimal liabilities) { this.liabilities = liabilities; }

    public BigDecimal getEquity() { return equity; }
    public void setEquity(BigDecimal equity) { this.equity = equity; }

    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }

    public BigDecimal getTotalLiabilitiesAndEquity() { return totalLiabilitiesAndEquity; }
    public void setTotalLiabilitiesAndEquity(BigDecimal totalLiabilitiesAndEquity) {
        this.totalLiabilitiesAndEquity = totalLiabilitiesAndEquity;
    }
}
