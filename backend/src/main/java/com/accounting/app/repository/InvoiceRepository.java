package com.accounting.app.repository;

import com.accounting.app.entity.Invoice;
import com.accounting.app.entity.Invoice.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 請求書リポジトリ
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * 会社IDで検索（請求日降順）
     * @param companyId 会社ID
     * @return 請求書リスト
     */
    List<Invoice> findByCompanyIdOrderByInvoiceDateDesc(Long companyId);

    /**
     * 会社IDとステータスで検索
     * @param companyId 会社ID
     * @param status ステータス
     * @return 請求書リスト
     */
    List<Invoice> findByCompanyIdAndStatusOrderByInvoiceDateDesc(Long companyId, InvoiceStatus status);

    /**
     * 会社IDと取引先IDで検索
     * @param companyId 会社ID
     * @param partnerId 取引先ID
     * @return 請求書リスト
     */
    List<Invoice> findByCompanyIdAndPartnerIdOrderByInvoiceDateDesc(Long companyId, Long partnerId);

    /**
     * 会社IDと請求日範囲で検索
     * @param companyId 会社ID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 請求書リスト
     */
    List<Invoice> findByCompanyIdAndInvoiceDateBetweenOrderByInvoiceDate(
            Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 会社IDと請求番号で検索
     * @param companyId 会社ID
     * @param invoiceNumber 請求番号
     * @return 請求書
     */
    Optional<Invoice> findByCompanyIdAndInvoiceNumber(Long companyId, String invoiceNumber);

    /**
     * 請求番号の存在確認
     * @param companyId 会社ID
     * @param invoiceNumber 請求番号
     * @return 存在する場合true
     */
    boolean existsByCompanyIdAndInvoiceNumber(Long companyId, String invoiceNumber);

    /**
     * 会社IDでページング検索
     * @param companyId 会社ID
     * @param pageable ページング情報
     * @return 請求書ページ
     */
    Page<Invoice> findAllByCompanyId(Long companyId, Pageable pageable);

    /**
     * 取引先IDでページング検索
     * @param partnerId 取引先ID
     * @param pageable ページング情報
     * @return 請求書ページ
     */
    Page<Invoice> findAllByPartnerId(Long partnerId, Pageable pageable);

    /**
     * 会社IDとステータスでページング検索
     * @param companyId 会社ID
     * @param status ステータス
     * @param pageable ページング情報
     * @return 請求書ページ
     */
    Page<Invoice> findAllByCompanyIdAndStatus(Long companyId, InvoiceStatus status, Pageable pageable);
}
