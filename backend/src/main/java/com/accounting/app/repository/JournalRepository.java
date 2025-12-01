package com.accounting.app.repository;

import com.accounting.app.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 仕訳リポジトリ
 */
@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    /**
     * 会社IDで検索（日付降順）
     * @param companyId 会社ID
     * @return 仕訳リスト
     */
    List<Journal> findByCompanyIdOrderByJournalDateDesc(Long companyId);

    /**
     * 会社IDと会計期間IDで検索
     * @param companyId 会社ID
     * @param fiscalPeriodId 会計期間ID
     * @return 仕訳リスト
     */
    List<Journal> findByCompanyIdAndFiscalPeriodIdOrderByJournalDateDesc(
            Long companyId, Long fiscalPeriodId);

    /**
     * 会社IDと日付範囲で検索
     * @param companyId 会社ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 仕訳リスト
     */
    List<Journal> findByCompanyIdAndJournalDateBetweenOrderByJournalDate(
            Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 会社IDと仕訳番号で検索
     * @param companyId 会社ID
     * @param journalNumber 仕訳番号
     * @return 仕訳
     */
    Optional<Journal> findByCompanyIdAndJournalNumber(Long companyId, String journalNumber);

    /**
     * 仕訳番号の存在確認
     * @param companyId 会社ID
     * @param journalNumber 仕訳番号
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndJournalNumber(Long companyId, String journalNumber);
}
