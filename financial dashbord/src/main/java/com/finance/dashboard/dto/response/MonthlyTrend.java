package com.finance.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyTrend {
    private int year;
    private int month;
    private String monthName;
    private double income;
    private double expense;
}
