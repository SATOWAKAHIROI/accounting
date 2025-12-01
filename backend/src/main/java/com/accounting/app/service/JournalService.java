package com.accounting.app.service;

import com.accounting.app.dto.request.JournalRequest;
import com.accounting.app.dto.request.JournalDetailRequest;
import com.accounting.app.dto.response.JournalResponse;
import com.accounting.app.entity.*;
import com.accounting.app.entity.JournalDetail.EntryType;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JournalService {

    private final JournalRepository journalRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final PartnerRepository partnerRepository;

    public JournalService(JournalRepository journalRepository,
                         JournalDetailRepository journalDetailRepository,
                         FiscalPeriodRepository fiscalPeriodRepository,
                         AccountRepository accountRepository,
                         SubAccountRepository subAccountRepository,
                         TaxTypeRepository taxTypeRepository,
                         PartnerRepository partnerRepository) {
        this.journalRepository = journalRepository;
        this.journalDetailRepository = journalDetailRepository;
        this.fiscalPeriodRepository = fiscalPeriodRepository;
        this.accountRepository = accountRepository;
        this.subAccountRepository = subAccountRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.partnerRepository = partnerRepository;
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> findAll(Long companyId) {
        return journalRepository.findByCompanyIdOrderByJournalDateDesc(companyId).stream()
                .map(JournalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JournalResponse findById(Long id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", "id", id));
        return JournalResponse.from(journal);
    }

    public JournalResponse create(Long companyId, JournalRequest request) {
        // 仕訳番号の重複チェック
        if (journalRepository.existsByCompanyIdAndJournalNumber(companyId, request.getJournalNumber())) {
            throw new BadRequestException("仕訳番号 '" + request.getJournalNumber() + "' は既に使用されています");
        }

        // 借方貸方バランスチェック
        validateDebitCreditBalance(request.getDetails());

        // 会計期間の取得と検証
        FiscalPeriod fiscalPeriod = fiscalPeriodRepository
                .findByCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        companyId, request.getJournalDate(), request.getJournalDate())
                .orElseThrow(() -> new BusinessException("FISCAL_PERIOD_NOT_FOUND",
                        "仕訳日に対応する会計期間が見つかりません"));

        if (fiscalPeriod.getIsClosed()) {
            throw new BusinessException("FISCAL_PERIOD_CLOSED",
                    "締められた会計期間には仕訳を登録できません");
        }

        // Journal作成
        Journal journal = new Journal();
        journal.setJournalDate(request.getJournalDate());
        journal.setJournalNumber(request.getJournalNumber());
        journal.setDescription(request.getDescription());
        journal.setFiscalPeriod(fiscalPeriod);

        Company company = new Company();
        company.setId(companyId);
        journal.setCompany(company);

        // JournalDetail作成
        for (JournalDetailRequest detailReq : request.getDetails()) {
            JournalDetail detail = createJournalDetail(detailReq);
            journal.addDetail(detail);
        }

        Journal saved = journalRepository.save(journal);
        return JournalResponse.from(saved);
    }

    public JournalResponse update(Long id, JournalRequest request) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", "id", id));

        // 会計期間の締め状態チェック
        if (journal.getFiscalPeriod().getIsClosed()) {
            throw new BusinessException("FISCAL_PERIOD_CLOSED",
                    "締められた会計期間の仕訳は変更できません");
        }

        // 借方貸方バランスチェック
        validateDebitCreditBalance(request.getDetails());

        // 仕訳番号変更時の重複チェック
        if (!journal.getJournalNumber().equals(request.getJournalNumber())) {
            if (journalRepository.existsByCompanyIdAndJournalNumber(
                    journal.getCompany().getId(), request.getJournalNumber())) {
                throw new BadRequestException("仕訳番号 '" + request.getJournalNumber() + "' は既に使用されています");
            }
        }

        journal.setJournalDate(request.getJournalDate());
        journal.setJournalNumber(request.getJournalNumber());
        journal.setDescription(request.getDescription());

        // 既存の明細を削除して新規作成
        journal.getDetails().clear();
        for (JournalDetailRequest detailReq : request.getDetails()) {
            JournalDetail detail = createJournalDetail(detailReq);
            journal.addDetail(detail);
        }

        Journal updated = journalRepository.save(journal);
        return JournalResponse.from(updated);
    }

    public void delete(Long id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", "id", id));

        if (journal.getFiscalPeriod().getIsClosed()) {
            throw new BusinessException("FISCAL_PERIOD_CLOSED",
                    "締められた会計期間の仕訳は削除できません");
        }

        journalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> findByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        return journalRepository.findByCompanyIdAndJournalDateBetweenOrderByJournalDate(
                companyId, startDate, endDate).stream()
                .map(JournalResponse::from)
                .collect(Collectors.toList());
    }

    private void validateDebitCreditBalance(List<JournalDetailRequest> details) {
        BigDecimal debitTotal = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;

        for (JournalDetailRequest detail : details) {
            if ("DEBIT".equals(detail.getEntryType())) {
                debitTotal = debitTotal.add(detail.getAmount());
            } else if ("CREDIT".equals(detail.getEntryType())) {
                creditTotal = creditTotal.add(detail.getAmount());
            }
        }

        if (debitTotal.compareTo(creditTotal) != 0) {
            throw new BusinessException("DEBIT_CREDIT_MISMATCH",
                    "借方合計と貸方合計が一致しません (借方: " + debitTotal + ", 貸方: " + creditTotal + ")");
        }
    }

    private JournalDetail createJournalDetail(JournalDetailRequest request) {
        JournalDetail detail = new JournalDetail();
        detail.setLineNumber(request.getLineNumber());
        detail.setEntryType(EntryType.valueOf(request.getEntryType()));
        detail.setAmount(request.getAmount());
        detail.setTaxAmount(request.getTaxAmount());
        detail.setDescription(request.getDescription());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
        detail.setAccount(account);

        if (request.getSubAccountId() != null) {
            SubAccount subAccount = subAccountRepository.findById(request.getSubAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubAccount", "id", request.getSubAccountId()));
            detail.setSubAccount(subAccount);
        }

        if (request.getTaxTypeId() != null) {
            TaxType taxType = taxTypeRepository.findById(request.getTaxTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TaxType", "id", request.getTaxTypeId()));
            detail.setTaxType(taxType);
        }

        if (request.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", request.getPartnerId()));
            detail.setPartner(partner);
        }

        return detail;
    }
}
