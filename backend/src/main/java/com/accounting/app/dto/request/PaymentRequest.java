package com.accounting.app.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequest {
    private Long invoiceId;
    @NotNull
    private Long partnerId;
    @NotNull
    private LocalDate paymentDate;
    @NotNull @Positive
    private BigDecimal amount;
    @NotNull
    private String paymentMethod; // BANK_TRANSFER, CASH, CREDIT_CARD, CHECK, OTHER
    private String notes;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
