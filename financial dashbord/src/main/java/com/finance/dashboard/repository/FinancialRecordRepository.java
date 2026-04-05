package com.finance.dashboard.repository;

import com.finance.dashboard.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    Optional<FinancialRecord> findByIdAndIsDeletedFalse(Long id);

    List<FinancialRecord> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM financial_records WHERE type = :type AND is_deleted = 0",
            nativeQuery = true)
    double sumByType(@Param("type") String type);

    @Query(value = "SELECT category, type, COALESCE(SUM(amount), 0) as total " +
            "FROM financial_records WHERE is_deleted = 0 " +
            "GROUP BY category, type ORDER BY category",
            nativeQuery = true)
    List<Object[]> categoryTotals();

    @Query(value = "SELECT YEAR(date) as yr, MONTH(date) as mo, type, COALESCE(SUM(amount), 0) as total " +
            "FROM financial_records WHERE is_deleted = 0 AND date >= :from " +
            "GROUP BY YEAR(date), MONTH(date), type ORDER BY YEAR(date), MONTH(date)",
            nativeQuery = true)
    List<Object[]> monthlyTrend(@Param("from") LocalDate from);

    @Query("SELECT COUNT(r) FROM FinancialRecord r WHERE r.isDeleted = false")
    long countActive();
}
