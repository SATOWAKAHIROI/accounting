package com.accounting.app.repository;

import com.accounting.app.entity.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 税区分リポジトリ
 */
@Repository
public interface TaxTypeRepository extends JpaRepository<TaxType, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 税区分リスト
     */
    List<TaxType> findByCompanyIdOrderByCode(Long companyId);

    /**
     * 会社IDとコードで検索
     * @param companyId 会社ID
     * @param code コード
     * @return 税区分
     */
    Optional<TaxType> findByCompanyIdAndCode(Long companyId, String code);

    /**
     * 指定日に有効な税区分を取得
     * @param companyId 会社ID
     * @param date 日付
     * @return 税区分リスト
     */
    @Query("SELECT t FROM TaxType t WHERE t.company.id = :companyId " +
           "AND t.effectiveFrom <= :date " +
           "AND (t.effectiveTo IS NULL OR t.effectiveTo >= :date)")
    List<TaxType> findEffectiveTaxTypes(@Param("companyId") Long companyId,
                                        @Param("date") LocalDate date);

    /**
     * コードの存在確認
     * @param companyId 会社ID
     * @param code コード
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndCode(Long companyId, String code);
}
