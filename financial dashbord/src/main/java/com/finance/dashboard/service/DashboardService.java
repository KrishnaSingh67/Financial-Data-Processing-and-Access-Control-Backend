package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.CategoryTotal;
import com.finance.dashboard.dto.response.DashboardSummary;
import com.finance.dashboard.dto.response.MonthlyTrend;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummary getSummary() {
        double totalIncome  = recordRepository.sumByType("INCOME");
        double totalExpense = recordRepository.sumByType("EXPENSE");
        long   total        = recordRepository.countActive();
        return new DashboardSummary(totalIncome, totalExpense, totalIncome - totalExpense, total);
    }

    public List<CategoryTotal> getCategoryTotals() {
        List<Object[]> rows = recordRepository.categoryTotals();
        // Aggregate into Map<category, CategoryTotal>
        Map<String, double[]> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String category = (String) row[0];
            String type     = (String) row[1];
            double amount   = ((Number) row[2]).doubleValue();
            map.computeIfAbsent(category, k -> new double[2]); // [income, expense]
            if ("INCOME".equals(type))  map.get(category)[0] += amount;
            if ("EXPENSE".equals(type)) map.get(category)[1] += amount;
        }
        List<CategoryTotal> result = new ArrayList<>();
        map.forEach((cat, vals) ->
                result.add(new CategoryTotal(cat, vals[0], vals[1], vals[0] - vals[1])));
        return result;
    }

    public List<MonthlyTrend> getMonthlyTrend() {
        LocalDate from = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        List<Object[]> rows = recordRepository.monthlyTrend(from);

        // Aggregate into Map<"year-month", MonthlyTrend>
        Map<String, double[]> map = new LinkedHashMap<>();
        Map<String, int[]>    ymMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            int year    = ((Number) row[0]).intValue();
            int month   = ((Number) row[1]).intValue();
            String type = (String) row[2];
            double amt  = ((Number) row[3]).doubleValue();
            String key  = year + "-" + String.format("%02d", month);
            map.computeIfAbsent(key, k -> new double[2]);
            ymMap.computeIfAbsent(key, k -> new int[]{year, month});
            if ("INCOME".equals(type))  map.get(key)[0] += amt;
            if ("EXPENSE".equals(type)) map.get(key)[1] += amt;
        }

        List<MonthlyTrend> result = new ArrayList<>();
        map.forEach((key, vals) -> {
            int[] ym = ymMap.get(key);
            String monthName = Month.of(ym[1]).name().substring(0, 3);
            result.add(new MonthlyTrend(ym[0], ym[1], monthName, vals[0], vals[1]));
        });
        return result;
    }

    public List<RecordResponse> getRecentActivity() {
        return recordRepository.findTop10ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream().map(RecordResponse::from).toList();
    }
}
