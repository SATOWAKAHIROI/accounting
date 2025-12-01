package com.accounting.app.dto.response;

import com.accounting.app.entity.Journal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class JournalResponse {
    private Long id;
    private LocalDate journalDate;
    private String journalNumber;
    private String description;
    private Long fiscalPeriodId;
    private List<JournalDetailResponse> details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JournalResponse from(Journal journal) {
        JournalResponse response = new JournalResponse();
        response.setId(journal.getId());
        response.setJournalDate(journal.getJournalDate());
        response.setJournalNumber(journal.getJournalNumber());
        response.setDescription(journal.getDescription());
        response.setFiscalPeriodId(journal.getFiscalPeriod().getId());
        response.setDetails(journal.getDetails().stream()
                .map(JournalDetailResponse::from)
                .collect(Collectors.toList()));
        response.setCreatedAt(journal.getCreatedAt());
        response.setUpdatedAt(journal.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getJournalDate() { return journalDate; }
    public void setJournalDate(LocalDate journalDate) { this.journalDate = journalDate; }
    public String getJournalNumber() { return journalNumber; }
    public void setJournalNumber(String journalNumber) { this.journalNumber = journalNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getFiscalPeriodId() { return fiscalPeriodId; }
    public void setFiscalPeriodId(Long fiscalPeriodId) { this.fiscalPeriodId = fiscalPeriodId; }
    public List<JournalDetailResponse> getDetails() { return details; }
    public void setDetails(List<JournalDetailResponse> details) { this.details = details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
