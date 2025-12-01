package com.accounting.app.service;

import com.accounting.app.dto.request.AccountRequest;
import com.accounting.app.dto.response.AccountResponse;
import com.accounting.app.entity.Account;
import com.accounting.app.entity.Account.AccountType;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.AccountRepository;
import com.accounting.app.repository.JournalDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 勘定科目管理サービス
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final JournalDetailRepository journalDetailRepository;

    public AccountService(AccountRepository accountRepository,
                         JournalDetailRepository journalDetailRepository) {
        this.accountRepository = accountRepository;
        this.journalDetailRepository = journalDetailRepository;
    }

    /**
     * 全件取得
     * @param companyId 会社ID
     * @return 勘定科目レスポンスリスト
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll(Long companyId) {
        List<Account> accounts = accountRepository.findByCompanyIdOrderByCode(companyId);
        return accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * ID検索
     * @param id 勘定科目ID
     * @return 勘定科目レスポンス
     */
    @Transactional(readOnly = true)
    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return AccountResponse.from(account);
    }

    /**
     * 作成
     * @param companyId 会社ID
     * @param request 勘定科目リクエスト
     * @return 勘定科目レスポンス
     */
    public AccountResponse create(Long companyId, AccountRequest request) {
        // コード重複チェック
        if (accountRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("勘定科目コード '" + request.getCode() + "' は既に使用されています");
        }

        // エンティティ作成
        Account account = new Account();
        account.setCode(request.getCode());
        account.setName(request.getName());
        account.setAccountType(AccountType.valueOf(request.getAccountType()));
        account.setIsSystem(request.getIsSystem() != null ? request.getIsSystem() : false);

        // CompanyエンティティはIDのみ設定（本来はCompanyRepositoryで取得すべきだが簡略化）
        com.accounting.app.entity.Company company = new com.accounting.app.entity.Company();
        company.setId(companyId);
        account.setCompany(company);

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.from(savedAccount);
    }

    /**
     * 更新
     * @param id 勘定科目ID
     * @param request 勘定科目リクエスト
     * @return 勘定科目レスポンス
     */
    public AccountResponse update(Long id, AccountRequest request) {
        // 存在確認
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        // システム勘定科目の場合、タイプ変更を禁止
        if (account.getIsSystem() &&
            !account.getAccountType().name().equals(request.getAccountType())) {
            throw new BusinessException("SYSTEM_ACCOUNT_READONLY",
                "システム勘定科目のタイプは変更できません");
        }

        // コード変更時の重複チェック
        if (!account.getCode().equals(request.getCode())) {
            if (accountRepository.existsByCompanyIdAndCode(
                    account.getCompany().getId(), request.getCode())) {
                throw new BadRequestException("勘定科目コード '" + request.getCode() + "' は既に使用されています");
            }
        }

        // 更新
        account.setCode(request.getCode());
        account.setName(request.getName());
        account.setAccountType(AccountType.valueOf(request.getAccountType()));

        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.from(updatedAccount);
    }

    /**
     * 削除
     * @param id 勘定科目ID
     */
    public void delete(Long id) {
        // 存在確認
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        // システム勘定科目の削除禁止
        if (account.getIsSystem()) {
            throw new BusinessException("SYSTEM_ACCOUNT_DELETE_FORBIDDEN",
                "システム勘定科目は削除できません");
        }

        // 仕訳明細で使用されているかチェック
        List<com.accounting.app.entity.JournalDetail> usedInJournals =
            journalDetailRepository.findByAccountId(id);
        if (!usedInJournals.isEmpty()) {
            throw new BusinessException("ACCOUNT_IN_USE",
                "この勘定科目は仕訳で使用されているため削除できません");
        }

        accountRepository.deleteById(id);
    }

    /**
     * 勘定科目タイプ別検索
     * @param companyId 会社ID
     * @param type 勘定科目タイプ
     * @return 勘定科目レスポンスリスト
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findByAccountType(Long companyId, AccountType type) {
        List<Account> accounts = accountRepository.findByCompanyIdAndAccountTypeOrderByCode(
            companyId, type);
        return accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }
}
