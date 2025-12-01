package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.dto.request.JournalRequest;
import com.accounting.app.dto.response.JournalResponse;
import com.accounting.app.service.JournalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/companies/{companyId}/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalResponse>>> findAll(
            @PathVariable Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<JournalResponse> journals;
        if (startDate != null && endDate != null) {
            journals = journalService.findByDateRange(companyId, startDate, endDate);
        } else {
            journals = journalService.findAll(companyId);
        }
        return ResponseEntity.ok(ApiResponse.success(journals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> findById(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        JournalResponse journal = journalService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(journal));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JournalResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody JournalRequest request) {
        JournalResponse journal = journalService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(journal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> update(
            @PathVariable Long companyId,
            @PathVariable Long id,
            @Valid @RequestBody JournalRequest request) {
        JournalResponse journal = journalService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(journal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        journalService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
