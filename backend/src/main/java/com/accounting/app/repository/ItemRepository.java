package com.accounting.app.repository;

import com.accounting.app.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 品目リポジトリ
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 品目リスト
     */
    List<Item> findByCompanyIdOrderByCode(Long companyId);

    /**
     * 会社IDとコードで検索
     * @param companyId 会社ID
     * @param code コード
     * @return 品目
     */
    Optional<Item> findByCompanyIdAndCode(Long companyId, String code);

    /**
     * コードの存在確認
     * @param companyId 会社ID
     * @param code コード
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndCode(Long companyId, String code);
}
