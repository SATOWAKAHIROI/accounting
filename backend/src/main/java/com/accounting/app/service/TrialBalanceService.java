package com.accounting.app.service;

import com.accounting.app.dto.response.TrialBalanceEntry;
import com.accounting.app.dto.response.TrialBalanceReport;
import com.accounting.app.entity.Account;
import com.accounting.app.entity.JournalDetail;
import com.accounting.app.repository.AccountRepository;
import com.accounting.app.repository.JournalDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 試算表サービス
 */
@Service
@Transactional(readOnly = true)
public class TrialBalanceService {

    private final JournalDetailRepository journalDetailRepository;
    private final AccountRepository accountRepository;

    public TrialBalanceService(
            JournalDetailRepository journalDetailRepository,
            AccountRepository accountRepository) {
        this.journalDetailRepository = journalDetailRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * 試算表を生成
     */
    public TrialBalanceReport generate(Long companyId, LocalDate asOfDate) {
        // 基準日までの全仕訳明細を取得
        List<JournalDetail> details = journalDetailRepository
                .findByCompanyIdAndJournalDateBeforeOrEqual(companyId, asOfDate);

        // 勘定科目ごとに集計
        Map<Long, AccountBalance> balanceMap = new HashMap<>();

        for (JournalDetail detail : details) {
            Account account = detail.getAccount();
            AccountBalance balance = balanceMap.computeIfAbsent(
                    account.getId(),
                    k -> new AccountBalance(account)
            );

            if (detail.getEntryType() == JournalDetail.EntryType.DEBIT) {
                balance.debitTotal = balance.debitTotal.add(detail.getAmount());
            } else {
                balance.creditTotal = balance.creditTotal.add(detail.getAmount());
            }
        }

        // エントリの作成
        List<TrialBalanceEntry> entries = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (AccountBalance balance : balanceMap.values()) {
            TrialBalanceEntry entry = new TrialBalanceEntry();
            entry.setAccountCode(balance.account.getCode());
            entry.setAccountName(balance.account.getName());
            entry.setAccountType(balance.account.getAccountType().name());

            // 借方残高と貸方残高の計算
            BigDecimal diff = balance.debitTotal.subtract(balance.creditTotal);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                // 借方残高
                entry.setDebitBalance(diff);
                entry.setCreditBalance(BigDecimal.ZERO);
                totalDebit = totalDebit.add(diff);
            } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                // 貸方残高
                entry.setDebitBalance(BigDecimal.ZERO);
                entry.setCreditBalance(diff.negate());
                totalCredit = totalCredit.add(diff.negate());
            } else {
                // 残高ゼロ
                entry.setDebitBalance(BigDecimal.ZERO);
                entry.setCreditBalance(BigDecimal.ZERO);
            }

            entries.add(entry);
        }

        // 勘定科目コードでソート
        entries.sort(Comparator.comparing(TrialBalanceEntry::getAccountCode));

        // レポートの作成
        TrialBalanceReport report = new TrialBalanceReport();
        report.setAsOfDate(asOfDate);
        report.setEntries(entries);
        report.setTotalDebit(totalDebit);
        report.setTotalCredit(totalCredit);
        report.setDifference(totalDebit.subtract(totalCredit));

        return report;
    }

    /**
     * 勘定科目残高の内部クラス
     */
    private static class AccountBalance {
        Account account;
        BigDecimal debitTotal = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;

        AccountBalance(Account account) {
            this.account = account;
        }
    }

    /**
     * PDF出力（実装予定）
     */
    public byte[] exportPdf(TrialBalanceReport report) {
        throw new UnsupportedOperationException("PDF出力機能は未実装です");
    }

    /**
     * Excel出力（実装予定）
     */
    public byte[] exportExcel(TrialBalanceReport report) {
        throw new UnsupportedOperationException("Excel出力機能は未実装です");
    }
}
