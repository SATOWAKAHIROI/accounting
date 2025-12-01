package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.response.*;
import com.accounting.app.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * レポート生成API
 */
@RestController
@RequestMapping("/companies/{companyId}/reports")
public class ReportController {

    private final GeneralLedgerService generalLedgerService;
    private final TrialBalanceService trialBalanceService;
    private final ProfitLossService profitLossService;
    private final BalanceSheetService balanceSheetService;

    public ReportController(
            GeneralLedgerService generalLedgerService,
            TrialBalanceService trialBalanceService,
            ProfitLossService profitLossService,
            BalanceSheetService balanceSheetService) {
        this.generalLedgerService = generalLedgerService;
        this.trialBalanceService = trialBalanceService;
        this.profitLossService = profitLossService;
        this.balanceSheetService = balanceSheetService;
    }

    /**
     * 総勘定元帳
     */
    @GetMapping("/general-ledger")
    public ResponseEntity<ApiResponse<GeneralLedgerReport>> getGeneralLedger(
            @PathVariable Long companyId,
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        GeneralLedgerReport report = generalLedgerService.generate(companyId, accountId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * 試算表
     */
    @GetMapping("/trial-balance")
    public ResponseEntity<ApiResponse<TrialBalanceReport>> getTrialBalance(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {

        TrialBalanceReport report = trialBalanceService.generate(companyId, asOfDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * 損益計算書
     */
    @GetMapping("/profit-loss")
    public ResponseEntity<ApiResponse<ProfitLossReport>> getProfitLoss(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ProfitLossReport report = profitLossService.generate(companyId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * 貸借対照表
     */
    @GetMapping("/balance-sheet")
    public ResponseEntity<ApiResponse<BalanceSheetReport>> getBalanceSheet(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {

        BalanceSheetReport report = balanceSheetService.generate(companyId, asOfDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
