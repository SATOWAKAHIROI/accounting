package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.FiscalPeriodRequest;
import com.accounting.app.dto.response.FiscalPeriodResponse;
import com.accounting.app.service.FiscalPeriodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/fiscal-periods")
public class FiscalPeriodController {

    private final FiscalPeriodService fiscalPeriodService;

    public FiscalPeriodController(FiscalPeriodService fiscalPeriodService) {
        this.fiscalPeriodService = fiscalPeriodService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FiscalPeriodResponse>>> findAll(@PathVariable Long companyId) {
        List<FiscalPeriodResponse> periods = fiscalPeriodService.findAll(companyId);
        return ResponseEntity.ok(ApiResponse.success(periods));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FiscalPeriodResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        FiscalPeriodResponse period = fiscalPeriodService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(period));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FiscalPeriodResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody FiscalPeriodRequest request) {
        FiscalPeriodResponse period = fiscalPeriodService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(period));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FiscalPeriodResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody FiscalPeriodRequest request) {
        FiscalPeriodResponse period = fiscalPeriodService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(period));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<FiscalPeriodResponse>> close(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        FiscalPeriodResponse period = fiscalPeriodService.closePeriod(id);
        return ResponseEntity.ok(ApiResponse.success(period));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<FiscalPeriodResponse>> reopen(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        FiscalPeriodResponse period = fiscalPeriodService.reopenPeriod(id);
        return ResponseEntity.ok(ApiResponse.success(period));
    }
}
