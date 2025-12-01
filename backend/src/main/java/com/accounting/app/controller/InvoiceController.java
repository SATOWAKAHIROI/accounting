package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.InvoiceRequest;
import com.accounting.app.dto.response.InvoiceResponse;
import com.accounting.app.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/companies/{companyId}/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> findAll(
            @PathVariable Long companyId,
            Pageable pageable) {
        Page<InvoiceResponse> invoices = invoiceService.findAllByCompanyId(companyId, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse invoice = invoiceService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(invoice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse invoice = invoiceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateStatus(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @RequestParam String status) {
        InvoiceResponse invoice = invoiceService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
