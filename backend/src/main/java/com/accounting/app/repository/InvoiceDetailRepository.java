package com.accounting.app.repository;

import com.accounting.app.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 請求書明細リポジトリ
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {

    /**
     * 請求書IDで検索
     * @param invoiceId 請求書ID
     * @return 請求書明細リスト
     */
    List<InvoiceDetail> findByInvoiceIdOrderByLineNumber(Long invoiceId);

    /**
     * 品目IDで検索
     * @param itemId 品目ID
     * @return 請求書明細リスト
     */
    List<InvoiceDetail> findByItemId(Long itemId);
}
