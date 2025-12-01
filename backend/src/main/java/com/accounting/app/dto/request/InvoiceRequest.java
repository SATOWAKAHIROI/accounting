package com.accounting.app.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class InvoiceRequest {
    @NotBlank @Size(max = 50)
    private String invoiceNumber;
    @NotNull
    private Long partnerId;
    @NotNull
    private LocalDate invoiceDate;
    @NotNull
    private LocalDate dueDate;
    private String notes;
    @NotEmpty @Valid
    private List<InvoiceDetailRequest> details;

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<InvoiceDetailRequest> getDetails() { return details; }
    public void setDetails(List<InvoiceDetailRequest> details) { this.details = details; }
}
