package com.accounting.app.repository;

import com.accounting.app.entity.JournalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 仕訳明細リポジトリ
 */
@Repository
public interface JournalDetailRepository extends JpaRepository<JournalDetail, Long> {

    /**
     * 仕訳IDで検索
     * @param journalId 仕訳ID
     * @return 仕訳明細リスト
     */
    List<JournalDetail> findByJournalIdOrderByLineNumber(Long journalId);

    /**
     * 勘定科目IDで検索
     * @param accountId 勘定科目ID
     * @return 仕訳明細リスト
     */
    List<JournalDetail> findByAccountId(Long accountId);

    /**
     * 勘定科目IDと日付範囲で検索（仕訳日でソート）
     * @param accountId 勘定科目ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 仕訳明細リスト
     */
    @Query("SELECT jd FROM JournalDetail jd JOIN FETCH jd.journal j " +
           "WHERE jd.account.id = :accountId AND j.journalDate BETWEEN :startDate AND :endDate " +
           "ORDER BY j.journalDate, j.id, jd.lineNumber")
    List<JournalDetail> findByAccountIdAndJournalDateBetweenOrderByJournalDate(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 勘定科目IDと日付（より前）で検索（仕訳日でソート）
     * @param accountId 勘定科目ID
     * @param beforeDate 基準日（この日より前）
     * @return 仕訳明細リスト
     */
    @Query("SELECT jd FROM JournalDetail jd JOIN FETCH jd.journal j " +
           "WHERE jd.account.id = :accountId AND j.journalDate < :beforeDate " +
           "ORDER BY j.journalDate, j.id, jd.lineNumber")
    List<JournalDetail> findByAccountIdAndJournalDateBeforeOrderByJournalDate(
            @Param("accountId") Long accountId,
            @Param("beforeDate") LocalDate beforeDate);

    /**
     * 会社IDと日付（以前）で検索
     * @param companyId 会社ID
     * @param asOfDate 基準日
     * @return 仕訳明細リスト
     */
    @Query("SELECT jd FROM JournalDetail jd JOIN FETCH jd.journal j JOIN FETCH jd.account a " +
           "WHERE j.company.id = :companyId AND j.journalDate <= :asOfDate")
    List<JournalDetail> findByCompanyIdAndJournalDateBeforeOrEqual(
            @Param("companyId") Long companyId,
            @Param("asOfDate") LocalDate asOfDate);

    /**
     * 会社IDと日付範囲で検索
     * @param companyId 会社ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 仕訳明細リスト
     */
    @Query("SELECT jd FROM JournalDetail jd JOIN FETCH jd.journal j JOIN FETCH jd.account a " +
           "WHERE j.company.id = :companyId AND j.journalDate BETWEEN :startDate AND :endDate")
    List<JournalDetail> findByCompanyIdAndJournalDateBetween(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
