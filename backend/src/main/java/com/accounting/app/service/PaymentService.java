package com.accounting.app.service;

import com.accounting.app.dto.request.PaymentRequest;
import com.accounting.app.dto.response.PaymentResponse;
import com.accounting.app.entity.*;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PartnerRepository partnerRepository;
    private final CompanyRepository companyRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            PartnerRepository partnerRepository,
            CompanyRepository companyRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.partnerRepository = partnerRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 入金を作成
     */
    public PaymentResponse create(Long companyId, PaymentRequest request) {
        // 会社の取得
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // 取引先の取得
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", request.getPartnerId()));

        // 入金エンティティの作成
        Payment payment = new Payment();
        payment.setCompany(company);
        payment.setPartner(partner);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount());
        payment.setNotes(request.getNotes());

        // 支払方法の設定
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.getPaymentMethod());
            payment.setPaymentMethod(method);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("無効な支払方法: " + request.getPaymentMethod());
        }

        // 請求書が指定されている場合
        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

            // 請求書のステータスチェック
            if (invoice.getStatus() != Invoice.InvoiceStatus.ISSUED) {
                throw new BusinessException("INVALID_INVOICE_STATUS",
                        "発行済みの請求書に対してのみ入金を登録できます");
            }

            // 請求書の取引先と入金の取引先が一致するかチェック
            if (!invoice.getPartner().getId().equals(partner.getId())) {
                throw new BusinessException("PARTNER_MISMATCH",
                        "請求書の取引先と入金の取引先が一致しません");
            }

            payment.setInvoice(invoice);

            // 入金額のバリデーション
            BigDecimal totalPayments = paymentRepository.findAllByInvoiceId(invoice.getId())
                    .stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal newTotal = totalPayments.add(request.getAmount());
            if (newTotal.compareTo(invoice.getTotalAmount()) > 0) {
                throw new BusinessException("PAYMENT_AMOUNT_EXCEEDED",
                        "入金額の合計が請求金額を超過しています（請求額: " + invoice.getTotalAmount() +
                        "、既存入金: " + totalPayments + "、今回入金: " + request.getAmount() + "）");
            }

            // 請求額と同額になった場合、請求書のステータスを「支払済み」に更新
            if (newTotal.compareTo(invoice.getTotalAmount()) == 0) {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoiceRepository.save(invoice);
            }
        }

        // 保存
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponse.from(savedPayment);
    }

    /**
     * 入金を更新
     */
    public PaymentResponse update(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        // 取引先の取得
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", request.getPartnerId()));

        // 請求書が紐付いている場合のチェック
        if (payment.getInvoice() != null && payment.getInvoice().getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new BusinessException("PAYMENT_UPDATE_FORBIDDEN",
                    "支払済みの請求書に紐付く入金は更新できません");
        }

        // 支払方法の設定
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.getPaymentMethod());
            payment.setPaymentMethod(method);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("無効な支払方法: " + request.getPaymentMethod());
        }

        // 更新前の請求書を保持
        Invoice oldInvoice = payment.getInvoice();
        BigDecimal oldAmount = payment.getAmount();

        // 入金情報を更新
        payment.setPartner(partner);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount());
        payment.setNotes(request.getNotes());

        // 請求書の変更処理
        Invoice newInvoice = null;
        if (request.getInvoiceId() != null) {
            newInvoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

            // 請求書のステータスチェック
            if (newInvoice.getStatus() != Invoice.InvoiceStatus.ISSUED) {
                throw new BusinessException("INVALID_INVOICE_STATUS",
                        "発行済みの請求書に対してのみ入金を登録できます");
            }

            // 請求書の取引先と入金の取引先が一致するかチェック
            if (!newInvoice.getPartner().getId().equals(partner.getId())) {
                throw new BusinessException("PARTNER_MISMATCH",
                        "請求書の取引先と入金の取引先が一致しません");
            }

            // 入金額のバリデーション（自身を除く）
            BigDecimal otherPayments = paymentRepository.findAllByInvoiceId(newInvoice.getId())
                    .stream()
                    .filter(p -> !p.getId().equals(id))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal newTotal = otherPayments.add(request.getAmount());
            if (newTotal.compareTo(newInvoice.getTotalAmount()) > 0) {
                throw new BusinessException("PAYMENT_AMOUNT_EXCEEDED",
                        "入金額の合計が請求金額を超過しています");
            }

            payment.setInvoice(newInvoice);

            // 請求額と同額になった場合、請求書のステータスを「支払済み」に更新
            if (newTotal.compareTo(newInvoice.getTotalAmount()) == 0) {
                newInvoice.setStatus(Invoice.InvoiceStatus.PAID);
            }
        } else {
            payment.setInvoice(null);
        }

        // 古い請求書のステータスを「発行済み」に戻す（金額が減った場合）
        if (oldInvoice != null && (newInvoice == null || !oldInvoice.getId().equals(newInvoice.getId()))) {
            if (oldInvoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                oldInvoice.setStatus(Invoice.InvoiceStatus.ISSUED);
                invoiceRepository.save(oldInvoice);
            }
        }

        // 保存
        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.from(updatedPayment);
    }

    /**
     * 入金を削除
     */
    public void delete(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        // 請求書が紐付いている場合のチェック
        if (payment.getInvoice() != null) {
            Invoice invoice = payment.getInvoice();

            // 支払済みの請求書に紐付く入金は削除不可
            if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                // 入金削除後のステータスを「発行済み」に戻す
                invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
                invoiceRepository.save(invoice);
            }
        }

        paymentRepository.delete(payment);
    }

    /**
     * 入金を取得
     */
    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return PaymentResponse.from(payment);
    }

    /**
     * 会社の入金一覧を取得
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllByCompanyId(Long companyId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByCompanyId(companyId, pageable);
        return payments.map(PaymentResponse::from);
    }

    /**
     * 請求書の入金一覧を取得
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAllByInvoiceId(Long invoiceId) {
        List<Payment> payments = paymentRepository.findAllByInvoiceId(invoiceId);
        return payments.stream()
                .map(PaymentResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 取引先の入金一覧を取得
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllByPartnerId(Long partnerId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByPartnerId(partnerId, pageable);
        return payments.map(PaymentResponse::from);
    }
}
