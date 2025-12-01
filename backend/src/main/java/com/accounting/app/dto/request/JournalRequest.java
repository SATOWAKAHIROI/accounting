package com.accounting.app.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 仕訳リクエストDTO
 */
public class JournalRequest {

    @NotNull(message = "仕訳日は必須です")
    private LocalDate journalDate;

    @NotBlank(message = "仕訳番号は必須です")
    @Size(max = 50, message = "仕訳番号は50文字以内で入力してください")
    private String journalNumber;

    @Size(max = 500, message = "摘要は500文字以内で入力してください")
    private String description;

    @NotEmpty(message = "仕訳明細は最低1行必要です")
    @Valid
    private List<JournalDetailRequest> details;

    // Getters and Setters
    public LocalDate getJournalDate() {
        return journalDate;
    }

    public void setJournalDate(LocalDate journalDate) {
        this.journalDate = journalDate;
    }

    public String getJournalNumber() {
        return journalNumber;
    }

    public void setJournalNumber(String journalNumber) {
        this.journalNumber = journalNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JournalDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<JournalDetailRequest> details) {
        this.details = details;
    }
}
