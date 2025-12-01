package com.accounting.app.service;

import com.accounting.app.dto.request.SubAccountRequest;
import com.accounting.app.dto.response.SubAccountResponse;
import com.accounting.app.entity.Account;
import com.accounting.app.entity.Company;
import com.accounting.app.entity.SubAccount;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.AccountRepository;
import com.accounting.app.repository.JournalDetailRepository;
import com.accounting.app.repository.SubAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 補助科目管理サービス
 */
@Service
@Transactional
public class SubAccountService {

    private final SubAccountRepository subAccountRepository;
    private final AccountRepository accountRepository;
    private final JournalDetailRepository journalDetailRepository;

    public SubAccountService(SubAccountRepository subAccountRepository,
                            AccountRepository accountRepository,
                            JournalDetailRepository journalDetailRepository) {
        this.subAccountRepository = subAccountRepository;
        this.accountRepository = accountRepository;
        this.journalDetailRepository = journalDetailRepository;
    }

    @Transactional(readOnly = true)
    public List<SubAccountResponse> findAll(Long companyId) {
        List<SubAccount> subAccounts = subAccountRepository.findByCompanyIdOrderByCode(companyId);
        return subAccounts.stream()
                .map(SubAccountResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubAccountResponse findById(Long id) {
        SubAccount subAccount = subAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubAccount", "id", id));
        return SubAccountResponse.from(subAccount);
    }

    public SubAccountResponse create(Long companyId, SubAccountRequest request) {
        // コード重複チェック
        if (subAccountRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("補助科目コード '" + request.getCode() + "' は既に使用されています");
        }

        // 勘定科目の存在確認
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        SubAccount subAccount = new SubAccount();
        subAccount.setCode(request.getCode());
        subAccount.setName(request.getName());
        subAccount.setAccount(account);

        Company company = new Company();
        company.setId(companyId);
        subAccount.setCompany(company);

        SubAccount saved = subAccountRepository.save(subAccount);
        return SubAccountResponse.from(saved);
    }

    public SubAccountResponse update(Long id, SubAccountRequest request) {
        SubAccount subAccount = subAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubAccount", "id", id));

        // コード変更時の重複チェック
        if (!subAccount.getCode().equals(request.getCode())) {
            if (subAccountRepository.existsByCompanyIdAndCode(
                    subAccount.getCompany().getId(), request.getCode())) {
                throw new BadRequestException("補助科目コード '" + request.getCode() + "' は既に使用されています");
            }
        }

        // 勘定科目の存在確認
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        subAccount.setCode(request.getCode());
        subAccount.setName(request.getName());
        subAccount.setAccount(account);

        SubAccount updated = subAccountRepository.save(subAccount);
        return SubAccountResponse.from(updated);
    }

    public void delete(Long id) {
        SubAccount subAccount = subAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubAccount", "id", id));

        // 仕訳明細で使用されているかチェック
        if (!journalDetailRepository.findByAccountId(id).isEmpty()) {
            throw new BusinessException("SUBACCOUNT_IN_USE",
                "この補助科目は仕訳で使用されているため削除できません");
        }

        subAccountRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SubAccountResponse> findByAccountId(Long accountId) {
        List<SubAccount> subAccounts = subAccountRepository.findByAccountIdOrderByCode(accountId);
        return subAccounts.stream()
                .map(SubAccountResponse::from)
                .collect(Collectors.toList());
    }
}
