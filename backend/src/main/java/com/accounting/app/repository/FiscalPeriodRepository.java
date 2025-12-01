package com.accounting.app.repository;

import com.accounting.app.entity.FiscalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 会計期間リポジトリ
 */
@Repository
public interface FiscalPeriodRepository extends JpaRepository<FiscalPeriod, Long> {

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return 会計期間リスト
     */
    List<FiscalPeriod> findByCompanyIdOrderByPeriodYearDescPeriodNumberDesc(Long companyId);

    /**
     * 会社IDと年度・期間番号で検索
     * @param companyId 会社ID
     * @param periodYear 年度
     * @param periodNumber 期間番号
     * @return 会計期間
     */
    Optional<FiscalPeriod> findByCompanyIdAndPeriodYearAndPeriodNumber(
            Long companyId, Integer periodYear, Integer periodNumber);

    /**
     * 日付が含まれる会計期間を検索
     * @param companyId 会社ID
     * @param date 日付
     * @return 会計期間
     */
    Optional<FiscalPeriod> findByCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long companyId, LocalDate date, LocalDate date2);

    /**
     * 会社IDで検索（開始日順）
     * @param companyId 会社ID
     * @return 会計期間リスト
     */
    List<FiscalPeriod> findByCompanyIdOrderByStartDate(Long companyId);

    /**
     * 期間重複チェック用クエリ
     * @param companyId 会社ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 重複する会計期間リスト
     */
    @Query("SELECT fp FROM FiscalPeriod fp WHERE fp.company.id = :companyId " +
           "AND ((fp.startDate <= :endDate AND fp.endDate >= :startDate))")
    List<FiscalPeriod> findByCompanyIdAndDateRangeOverlapping(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
