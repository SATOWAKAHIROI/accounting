package com.accounting.app.service;

import com.accounting.app.dto.response.ProfitLossReport;
import com.accounting.app.entity.Account;
import com.accounting.app.entity.JournalDetail;
import com.accounting.app.repository.JournalDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 損益計算書サービス
 */
@Service
@Transactional(readOnly = true)
public class ProfitLossService {

    private final JournalDetailRepository journalDetailRepository;

    public ProfitLossService(JournalDetailRepository journalDetailRepository) {
        this.journalDetailRepository = journalDetailRepository;
    }

    /**
     * 損益計算書を生成
     */
    public ProfitLossReport generate(Long companyId, LocalDate startDate, LocalDate endDate) {
        // 期間内の仕訳明細を取得
        List<JournalDetail> details = journalDetailRepository
                .findByCompanyIdAndJournalDateBetween(companyId, startDate, endDate);

        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (JournalDetail detail : details) {
            Account account = detail.getAccount();
            Account.AccountType accountType = account.getAccountType();

            if (accountType == Account.AccountType.REVENUE) {
                // 収益: 貸方増加、借方減少
                if (detail.getEntryType() == JournalDetail.EntryType.CREDIT) {
                    revenue = revenue.add(detail.getAmount());
                } else {
                    revenue = revenue.subtract(detail.getAmount());
                }
            } else if (accountType == Account.AccountType.EXPENSE) {
                // 費用: 借方増加、貸方減少
                if (detail.getEntryType() == JournalDetail.EntryType.DEBIT) {
                    expense = expense.add(detail.getAmount());
                } else {
                    expense = expense.subtract(detail.getAmount());
                }
            }
        }

        // レポートの作成
        ProfitLossReport report = new ProfitLossReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setRevenue(revenue);
        report.setExpense(expense);
        report.setNetProfit(revenue.subtract(expense));

        return report;
    }

    /**
     * PDF出力（実装予定）
     */
    public byte[] exportPdf(ProfitLossReport report) {
        throw new UnsupportedOperationException("PDF出力機能は未実装です");
    }

    /**
     * Excel出力（実装予定）
     */
    public byte[] exportExcel(ProfitLossReport report) {
        throw new UnsupportedOperationException("Excel出力機能は未実装です");
    }
}
