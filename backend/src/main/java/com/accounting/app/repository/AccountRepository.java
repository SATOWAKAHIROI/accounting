package com.accounting.app.repository;

import com.accounting.app.entity.Account;
import com.accounting.app.entity.Account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 勘定科目リポジトリ
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 勘定科目リスト
     */
    List<Account> findByCompanyIdOrderByCode(Long companyId);

    /**
     * 会社IDと勘定科目タイプで検索
     * @param companyId 会社ID
     * @param accountType 勘定科目タイプ
     * @return 勘定科目リスト
     */
    List<Account> findByCompanyIdAndAccountTypeOrderByCode(Long companyId, AccountType accountType);

    /**
     * 会社IDとコードで検索
     * @param companyId 会社ID
     * @param code コード
     * @return 勘定科目
     */
    Optional<Account> findByCompanyIdAndCode(Long companyId, String code);

    /**
     * コードの存在確認
     * @param companyId 会社ID
     * @param code コード
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndCode(Long companyId, String code);

    /**
     * 会社IDの勘定科目数をカウント
     * @param companyId 会社ID
     * @return 勘定科目数
     */
    long countByCompanyId(Long companyId);
}
