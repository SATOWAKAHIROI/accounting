package com.accounting.app.repository;

import com.accounting.app.entity.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 補助科目リポジトリ
 */
@Repository
public interface SubAccountRepository extends JpaRepository<SubAccount, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 補助科目リスト
     */
    List<SubAccount> findByCompanyIdOrderByCode(Long companyId);

    /**
     * 勘定科目IDで検索
     * @param accountId 勘定科目ID
     * @return 補助科目リスト
     */
    List<SubAccount> findByAccountIdOrderByCode(Long accountId);

    /**
     * 会社IDとコードで検索
     * @param companyId 会社ID
     * @param code コード
     * @return 補助科目
     */
    Optional<SubAccount> findByCompanyIdAndCode(Long companyId, String code);

    /**
     * コードの存在確認
     * @param companyId 会社ID
     * @param code コード
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndCode(Long companyId, String code);
}
