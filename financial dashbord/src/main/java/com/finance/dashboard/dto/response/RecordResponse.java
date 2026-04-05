package com.finance.dashboard.dto.response;

import com.finance.dashboard.entity.FinancialRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecordResponse {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecordResponse from(FinancialRecord r) {
        RecordResponse resp = new RecordResponse();
        resp.setId(r.getId());
        resp.setAmount(r.getAmount());
        resp.setType(r.getType().name());
        resp.setCategory(r.getCategory());
        resp.setDate(r.getDate());
        resp.setNotes(r.getNotes());
        resp.setCreatedByName(r.getCreatedBy() != null ? r.getCreatedBy().getName() : null);
        resp.setCreatedAt(r.getCreatedAt());
        resp.setUpdatedAt(r.getUpdatedAt());
        return resp;
    }
}
