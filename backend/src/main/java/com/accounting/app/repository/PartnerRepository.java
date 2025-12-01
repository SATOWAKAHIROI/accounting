package com.accounting.app.repository;

import com.accounting.app.entity.Partner;
import com.accounting.app.entity.Partner.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 取引先リポジトリ
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 取引先リスト
     */
    List<Partner> findByCompanyIdOrderByCode(Long companyId);

    /**
     * 会社IDと取引先タイプで検索
     * @param companyId 会社ID
     * @param partnerType 取引先タイプ
     * @return 取引先リスト
     */
    List<Partner> findByCompanyIdAndPartnerTypeOrderByCode(Long companyId, PartnerType partnerType);

    /**
     * 会社IDとコードで検索
     * @param companyId 会社ID
     * @param code コード
     * @return 取引先
     */
    Optional<Partner> findByCompanyIdAndCode(Long companyId, String code);

    /**
     * コードの存在確認
     * @param companyId 会社ID
     * @param code コード
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndCode(Long companyId, String code);
}
