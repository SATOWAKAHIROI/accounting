package com.accounting.app.service;

import com.accounting.app.dto.request.InvoiceDetailRequest;
import com.accounting.app.dto.request.InvoiceRequest;
import com.accounting.app.dto.response.InvoiceResponse;
import com.accounting.app.entity.*;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.CompanyRepository;
import com.accounting.app.repository.InvoiceDetailRepository;
import com.accounting.app.repository.InvoiceRepository;
import com.accounting.app.repository.PartnerRepository;
import com.accounting.app.repository.ItemRepository;
import com.accounting.app.repository.TaxTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final PartnerRepository partnerRepository;
    private final ItemRepository itemRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final CompanyRepository companyRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            InvoiceDetailRepository invoiceDetailRepository,
            PartnerRepository partnerRepository,
            ItemRepository itemRepository,
            TaxTypeRepository taxTypeRepository,
            CompanyRepository companyRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.partnerRepository = partnerRepository;
        this.itemRepository = itemRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 請求書を作成
     */
    public InvoiceResponse create(Long companyId, InvoiceRequest request) {
        // 会社の取得
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // 取引先の取得
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", request.getPartnerId()));

        // 請求書番号の重複チェック
        if (invoiceRepository.existsByCompanyIdAndInvoiceNumber(companyId, request.getInvoiceNumber())) {
            throw new BadRequestException("請求書番号 '" + request.getInvoiceNumber() + "' は既に使用されています");
        }

        // 請求日と支払期限の妥当性チェック
        if (request.getDueDate().isBefore(request.getInvoiceDate())) {
            throw new BadRequestException("支払期限は請求日以降である必要があります");
        }

        // 請求書エンティティの作成
        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setPartner(partner);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);

        // 明細の作成と金額計算
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceDetailRequest detailReq : request.getDetails()) {
            InvoiceDetail detail = createInvoiceDetail(invoice, detailReq);
            invoice.addDetail(detail);
            subtotal = subtotal.add(detail.getAmount().subtract(detail.getTaxAmount()));
            totalTax = totalTax.add(detail.getTaxAmount());
        }

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        // 保存
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponse.from(savedInvoice);
    }

    /**
     * 請求書明細を作成
     */
    private InvoiceDetail createInvoiceDetail(Invoice invoice, InvoiceDetailRequest request) {
        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setLineNumber(request.getLineNumber());
        detail.setDescription(request.getDescription());
        detail.setQuantity(request.getQuantity());
        detail.setUnitPrice(request.getUnitPrice());

        // 品目が指定されている場合
        if (request.getItemId() != null) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item", "id", request.getItemId()));
            detail.setItem(item);
        }

        // 税区分が指定されている場合
        TaxType taxType = null;
        if (request.getTaxTypeId() != null) {
            taxType = taxTypeRepository.findById(request.getTaxTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TaxType", "id", request.getTaxTypeId()));
            detail.setTaxType(taxType);
        }

        // 金額計算
        BigDecimal lineAmount = request.getQuantity().multiply(request.getUnitPrice());
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (taxType != null) {
            taxAmount = lineAmount.multiply(taxType.getTaxRate()).divide(new BigDecimal("100"));
        }

        detail.setTaxAmount(taxAmount);
        detail.setAmount(lineAmount.add(taxAmount));

        return detail;
    }

    /**
     * 請求書を更新
     */
    public InvoiceResponse update(Long id, InvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        // ステータスチェック（発行済み・支払済み・キャンセル済みは更新不可）
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new BusinessException("INVOICE_NOT_EDITABLE",
                    "下書き状態の請求書のみ更新できます");
        }

        // 取引先の取得
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", request.getPartnerId()));

        // 請求書番号の重複チェック（自身以外）
        invoiceRepository.findByCompanyIdAndInvoiceNumber(
                invoice.getCompany().getId(), request.getInvoiceNumber())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("請求書番号 '" + request.getInvoiceNumber() + "' は既に使用されています");
                    }
                });

        // 請求日と支払期限の妥当性チェック
        if (request.getDueDate().isBefore(request.getInvoiceDate())) {
            throw new BadRequestException("支払期限は請求日以降である必要があります");
        }

        // 請求書の更新
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setPartner(partner);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setNotes(request.getNotes());

        // 既存の明細を削除
        invoice.getDetails().clear();
        invoiceDetailRepository.flush();

        // 新しい明細の追加と金額再計算
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceDetailRequest detailReq : request.getDetails()) {
            InvoiceDetail detail = createInvoiceDetail(invoice, detailReq);
            invoice.addDetail(detail);
            subtotal = subtotal.add(detail.getAmount().subtract(detail.getTaxAmount()));
            totalTax = totalTax.add(detail.getTaxAmount());
        }

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        // 保存
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponse.from(updatedInvoice);
    }

    /**
     * 請求書のステータスを更新
     */
    public InvoiceResponse updateStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        try {
            Invoice.InvoiceStatus newStatus = Invoice.InvoiceStatus.valueOf(status);

            // ステータス遷移のバリデーション
            validateStatusTransition(invoice.getStatus(), newStatus);

            invoice.setStatus(newStatus);
            Invoice updatedInvoice = invoiceRepository.save(invoice);
            return InvoiceResponse.from(updatedInvoice);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("無効なステータス: " + status);
        }
    }

    /**
     * ステータス遷移の妥当性チェック
     */
    private void validateStatusTransition(Invoice.InvoiceStatus current, Invoice.InvoiceStatus newStatus) {
        // DRAFT -> ISSUED, CANCELED
        // ISSUED -> PAID, CANCELED
        // PAID -> (変更不可)
        // CANCELED -> (変更不可)

        if (current == Invoice.InvoiceStatus.PAID) {
            throw new BusinessException("STATUS_CHANGE_FORBIDDEN",
                    "支払済みの請求書のステータスは変更できません");
        }

        if (current == Invoice.InvoiceStatus.CANCELED) {
            throw new BusinessException("STATUS_CHANGE_FORBIDDEN",
                    "キャンセル済みの請求書のステータスは変更できません");
        }

        if (current == Invoice.InvoiceStatus.DRAFT) {
            if (newStatus != Invoice.InvoiceStatus.ISSUED && newStatus != Invoice.InvoiceStatus.CANCELED) {
                throw new BusinessException("INVALID_STATUS_TRANSITION",
                        "下書きから遷移できるのは「発行済み」または「キャンセル」のみです");
            }
        }

        if (current == Invoice.InvoiceStatus.ISSUED) {
            if (newStatus != Invoice.InvoiceStatus.PAID && newStatus != Invoice.InvoiceStatus.CANCELED) {
                throw new BusinessException("INVALID_STATUS_TRANSITION",
                        "発行済みから遷移できるのは「支払済み」または「キャンセル」のみです");
            }
        }
    }

    /**
     * 請求書を削除
     */
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        // ステータスチェック（下書きのみ削除可能）
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new BusinessException("INVOICE_DELETE_FORBIDDEN",
                    "下書き状態の請求書のみ削除できます");
        }

        invoiceRepository.delete(invoice);
    }

    /**
     * 請求書を取得
     */
    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return InvoiceResponse.from(invoice);
    }

    /**
     * 会社の請求書一覧を取得
     */
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> findAllByCompanyId(Long companyId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findAllByCompanyId(companyId, pageable);
        return invoices.map(InvoiceResponse::from);
    }

    /**
     * 取引先の請求書一覧を取得
     */
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> findAllByPartnerId(Long partnerId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findAllByPartnerId(partnerId, pageable);
        return invoices.map(InvoiceResponse::from);
    }

    /**
     * ステータス別の請求書一覧を取得
     */
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> findAllByStatus(Long companyId, String status, Pageable pageable) {
        try {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status);
            Page<Invoice> invoices = invoiceRepository.findAllByCompanyIdAndStatus(companyId, invoiceStatus, pageable);
            return invoices.map(InvoiceResponse::from);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("無効なステータス: " + status);
        }
    }
}
