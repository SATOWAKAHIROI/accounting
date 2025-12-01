package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.PartnerRequest;
import com.accounting.app.dto.response.PartnerResponse;
import com.accounting.app.service.PartnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartnerResponse>>> findAll(@PathVariable Long companyId) {
        List<PartnerResponse> partners = partnerService.findAll(companyId);
        return ResponseEntity.ok(ApiResponse.success(partners));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartnerResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        PartnerResponse partner = partnerService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(partner));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PartnerResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody PartnerRequest request) {
        PartnerResponse partner = partnerService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(partner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PartnerResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody PartnerRequest request) {
        PartnerResponse partner = partnerService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(partner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        partnerService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
