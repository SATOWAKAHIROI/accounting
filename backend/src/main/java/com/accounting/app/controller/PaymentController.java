package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.PaymentRequest;
import com.accounting.app.dto.response.PaymentResponse;
import com.accounting.app.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> findAll(
            @PathVariable Long companyId,
            Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.findAllByCompanyId(companyId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        PaymentResponse payment = paymentService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/by-invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> findByInvoiceId(
            @PathVariable Long companyId,
            @PathVariable Long invoiceId) {
        List<PaymentResponse> payments = paymentService.findAllByInvoiceId(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(payment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
