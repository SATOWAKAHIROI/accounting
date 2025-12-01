package com.accounting.app.service;

import com.accounting.app.dto.response.GeneralLedgerEntry;
import com.accounting.app.dto.response.GeneralLedgerReport;
import com.accounting.app.entity.*;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 総勘定元帳サービス
 */
@Service
@Transactional(readOnly = true)
public class GeneralLedgerService {

    private final JournalDetailRepository journalDetailRepository;
        private final AccountRepository accountRepository;
        private final JournalRepository journalRepository;

    public GeneralLedgerService(
            JournalDetailRepository journalDetailRepository,
            AccountRepository accountRepository,
            JournalRepository journalRepository) {
        this.journalDetailRepository = journalDetailRepository;
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
    }

    /**
     * 総勘定元帳を生成
     */
    public GeneralLedgerReport generate(Long companyId, Long accountId, LocalDate startDate, LocalDate endDate) {
        // 勘定科目の取得
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        // レポートの初期化
        GeneralLedgerReport report = new GeneralLedgerReport();
        report.setAccountCode(account.getCode());
        report.setAccountName(account.getName());
        report.setAccountType(account.getAccountType().name());
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        // 期首残高の計算（startDateより前の取引の累積）
        BigDecimal openingBalance = calculateOpeningBalance(accountId, startDate, account.getAccountType());
        report.setOpeningBalance(openingBalance);

        // 期間内の取引を取得
        List<JournalDetail> details = journalDetailRepository
                .findByAccountIdAndJournalDateBetweenOrderByJournalDate(accountId, startDate, endDate);

        // エントリの作成と残高計算
        List<GeneralLedgerEntry> entries = new ArrayList<>();
        BigDecimal runningBalance = openingBalance;

        for (JournalDetail detail : details) {
            GeneralLedgerEntry entry = new GeneralLedgerEntry();
            entry.setDate(detail.getJournal().getJournalDate());
            entry.setJournalNumber(detail.getJournal().getJournalNumber());
            entry.setDescription(detail.getDescription() != null ? detail.getDescription() : detail.getJournal().getDescription());

            BigDecimal debitAmount = BigDecimal.ZERO;
            BigDecimal creditAmount = BigDecimal.ZERO;

            if (detail.getEntryType() == JournalDetail.EntryType.DEBIT) {
                debitAmount = detail.getAmount();
            } else {
                creditAmount = detail.getAmount();
            }

            entry.setDebitAmount(debitAmount);
            entry.setCreditAmount(creditAmount);

            // 残高計算（勘定科目タイプに応じて計算方法が異なる）
            runningBalance = calculateBalance(runningBalance, debitAmount, creditAmount, account.getAccountType());
            entry.setBalance(runningBalance);

            entries.add(entry);
        }

        report.setEntries(entries);
        report.setClosingBalance(runningBalance);

        return report;
    }

    /**
     * 期首残高を計算
     */
    private BigDecimal calculateOpeningBalance(Long accountId, LocalDate startDate, Account.AccountType accountType) {
        List<JournalDetail> priorDetails = journalDetailRepository
                .findByAccountIdAndJournalDateBeforeOrderByJournalDate(accountId, startDate);

        BigDecimal balance = BigDecimal.ZERO;

        for (JournalDetail detail : priorDetails) {
            BigDecimal debitAmount = detail.getEntryType() == JournalDetail.EntryType.DEBIT ? detail.getAmount() : BigDecimal.ZERO;
            BigDecimal creditAmount = detail.getEntryType() == JournalDetail.EntryType.CREDIT ? detail.getAmount() : BigDecimal.ZERO;
            balance = calculateBalance(balance, debitAmount, creditAmount, accountType);
        }

        return balance;
    }

    /**
     * 残高を計算
     * - 資産・費用: 残高 += 借方 - 貸方
     * - 負債・純資産・収益: 残高 += 貸方 - 借方
     */
    private BigDecimal calculateBalance(BigDecimal currentBalance, BigDecimal debitAmount, BigDecimal creditAmount, Account.AccountType accountType) {
        if (accountType == Account.AccountType.ASSET || accountType == Account.AccountType.EXPENSE) {
            // 資産・費用: 借方増加、貸方減少
            return currentBalance.add(debitAmount).subtract(creditAmount);
        } else {
            // 負債・純資産・収益: 貸方増加、借方減少
            return currentBalance.add(creditAmount).subtract(debitAmount);
        }
    }

    /**
     * PDF出力（実装予定）
     * TODO: Apache PDFBoxを使用してPDF生成を実装
     */
    public byte[] exportPdf(GeneralLedgerReport report) {
        // PDF生成ロジックをここに実装
        throw new UnsupportedOperationException("PDF出力機能は未実装です");
    }

    /**
     * Excel出力（実装予定）
     * TODO: Apache POIを使用してExcel生成を実装
     */
    public byte[] exportExcel(GeneralLedgerReport report) {
        // Excel生成ロジックをここに実装
        throw new UnsupportedOperationException("Excel出力機能は未実装です");
    }
}
