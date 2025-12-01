package com.accounting.app.service;

import com.accounting.app.dto.response.BalanceSheetReport;
import com.accounting.app.dto.response.ProfitLossReport;
import com.accounting.app.entity.Account;
import com.accounting.app.entity.FiscalPeriod;
import com.accounting.app.entity.JournalDetail;
import com.accounting.app.repository.FiscalPeriodRepository;
import com.accounting.app.repository.JournalDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 貸借対照表サービス
 */
@Service
@Transactional(readOnly = true)
public class BalanceSheetService {

    private final JournalDetailRepository journalDetailRepository;
    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final ProfitLossService profitLossService;

    public BalanceSheetService(
            JournalDetailRepository journalDetailRepository,
            FiscalPeriodRepository fiscalPeriodRepository,
            ProfitLossService profitLossService) {
        this.journalDetailRepository = journalDetailRepository;
        this.fiscalPeriodRepository = fiscalPeriodRepository;
        this.profitLossService = profitLossService;
    }

    /**
     * 貸借対照表を生成
     */
    public BalanceSheetReport generate(Long companyId, LocalDate asOfDate) {
        // 基準日までの仕訳明細を取得
        List<JournalDetail> details = journalDetailRepository
                .findByCompanyIdAndJournalDateBeforeOrEqual(companyId, asOfDate);

        BigDecimal assets = BigDecimal.ZERO;
        BigDecimal liabilities = BigDecimal.ZERO;
        BigDecimal equity = BigDecimal.ZERO;

        for (JournalDetail detail : details) {
            Account account = detail.getAccount();
            Account.AccountType accountType = account.getAccountType();

            BigDecimal amount = BigDecimal.ZERO;

            if (accountType == Account.AccountType.ASSET) {
                // 資産: 借方増加、貸方減少
                if (detail.getEntryType() == JournalDetail.EntryType.DEBIT) {
                    amount = detail.getAmount();
                } else {
                    amount = detail.getAmount().negate();
                }
                assets = assets.add(amount);
            } else if (accountType == Account.AccountType.LIABILITY) {
                // 負債: 貸方増加、借方減少
                if (detail.getEntryType() == JournalDetail.EntryType.CREDIT) {
                    amount = detail.getAmount();
                } else {
                    amount = detail.getAmount().negate();
                }
                liabilities = liabilities.add(amount);
            } else if (accountType == Account.AccountType.EQUITY) {
                // 純資産: 貸方増加、借方減少
                if (detail.getEntryType() == JournalDetail.EntryType.CREDIT) {
                    amount = detail.getAmount();
                } else {
                    amount = detail.getAmount().negate();
                }
                equity = equity.add(amount);
            }
        }

        // 当期純利益を計算（会計期間の開始日から基準日まで）
        // 基準日が含まれる会計期間を検索
        Optional<FiscalPeriod> fiscalPeriodOpt = fiscalPeriodRepository
                .findByCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        companyId, asOfDate, asOfDate);

        LocalDate fiscalYearStart;
        if (fiscalPeriodOpt.isPresent()) {
            fiscalYearStart = fiscalPeriodOpt.get().getStartDate();
        } else {
            // 会計期間が見つからない場合は、その年の1月1日をデフォルトとする
            fiscalYearStart = LocalDate.of(asOfDate.getYear(), 1, 1);
        }

        ProfitLossReport plReport = profitLossService.generate(companyId, fiscalYearStart, asOfDate);
        BigDecimal netProfit = plReport.getNetProfit();

        // レポートの作成
        BalanceSheetReport report = new BalanceSheetReport();
        report.setAsOfDate(asOfDate);
        report.setAssets(assets);
        report.setLiabilities(liabilities);
        report.setEquity(equity);
        report.setNetProfit(netProfit);
        report.setTotalLiabilitiesAndEquity(liabilities.add(equity).add(netProfit));

        return report;
    }

    /**
     * PDF出力（実装予定）
     */
    public byte[] exportPdf(BalanceSheetReport report) {
        throw new UnsupportedOperationException("PDF出力機能は未実装です");
    }

    /**
     * Excel出力（実装予定）
     */
    public byte[] exportExcel(BalanceSheetReport report) {
        throw new UnsupportedOperationException("Excel出力機能は未実装です");
    }
}
