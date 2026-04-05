package com.finance.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryTotal {
    private String category;
    private double income;
    private double expense;
    private double net;
}
