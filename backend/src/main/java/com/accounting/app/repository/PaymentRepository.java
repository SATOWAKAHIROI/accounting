package com.accounting.app.repository;

import com.accounting.app.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 入金リポジトリ
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 会社IDで検索（入金日降順）
     * @param companyId 会社ID
     * @return 入金リスト
     */
    List<Payment> findByCompanyIdOrderByPaymentDateDesc(Long companyId);

    /**
     * 会社IDと請求書IDで検索
     * @param companyId 会社ID
     * @param invoiceId 請求書ID
     * @return 入金リスト
     */
    List<Payment> findByCompanyIdAndInvoiceIdOrderByPaymentDateDesc(Long companyId, Long invoiceId);

    /**
     * 会社IDと取引先IDで検索
     * @param companyId 会社ID
     * @param partnerId 取引先ID
     * @return 入金リスト
     */
    List<Payment> findByCompanyIdAndPartnerIdOrderByPaymentDateDesc(Long companyId, Long partnerId);

    /**
     * 会社IDと入金日範囲で検索
     * @param companyId 会社ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 入金リスト
     */
    List<Payment> findByCompanyIdAndPaymentDateBetweenOrderByPaymentDate(
            Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 請求書IDで検索
     * @param invoiceId 請求書ID
     * @return 入金リスト
     */
    List<Payment> findAllByInvoiceId(Long invoiceId);

    /**
     * 会社IDでページング検索
     * @param companyId 会社ID
     * @param pageable ページング情報
     * @return 入金ページ
     */
    Page<Payment> findAllByCompanyId(Long companyId, Pageable pageable);

    /**
     * 取引先IDでページング検索
     * @param partnerId 取引先ID
     * @param pageable ページング情報
     * @return 入金ページ
     */
    Page<Payment> findAllByPartnerId(Long partnerId, Pageable pageable);
}
