package com.finance.dashboard.controller;

import com.finance.dashboard.dto.response.CategoryTotal;
import com.finance.dashboard.dto.response.DashboardSummary;
import com.finance.dashboard.dto.response.MonthlyTrend;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Overview: total income, expenses, net balance, record count
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    /**
     * Income and expense totals broken down by category
     */
    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryTotal>> getByCategoryTotals() {
        return ResponseEntity.ok(dashboardService.getCategoryTotals());
    }

    /**
     * Monthly income vs expense for the past 12 months
     */
    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrend() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrend());
    }

    /**
     * Last 10 financial transactions
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponse>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
