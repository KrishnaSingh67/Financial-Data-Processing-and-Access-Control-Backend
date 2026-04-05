package com.finance.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummary {
    private double totalIncome;
    private double totalExpense;
    private double netBalance;
    private long totalRecords;
}
