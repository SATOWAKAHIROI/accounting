package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.TaxTypeRequest;
import com.accounting.app.dto.response.TaxTypeResponse;
import com.accounting.app.service.TaxTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/tax-types")
public class TaxTypeController {

    private final TaxTypeService taxTypeService;

    public TaxTypeController(TaxTypeService taxTypeService) {
        this.taxTypeService = taxTypeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxTypeResponse>>> findAll(@PathVariable Long companyId) {
        List<TaxTypeResponse> taxTypes = taxTypeService.findAll(companyId);
        return ResponseEntity.ok(ApiResponse.success(taxTypes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxTypeResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        TaxTypeResponse taxType = taxTypeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(taxType));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaxTypeResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody TaxTypeRequest request) {
        TaxTypeResponse taxType = taxTypeService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(taxType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxTypeResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody TaxTypeRequest request) {
        TaxTypeResponse taxType = taxTypeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(taxType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        taxTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
