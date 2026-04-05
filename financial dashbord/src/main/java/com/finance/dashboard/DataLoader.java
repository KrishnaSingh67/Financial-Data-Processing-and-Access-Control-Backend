package com.finance.dashboard;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.enums.Role;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            return;
        }

        log.info("Seeding database with sample data...");
        String encoded = passwordEncoder.encode("password123");

        User admin = userRepository.save(User.builder()
                .name("Admin User").email("admin@fin.dev")
                .password(encoded).role(Role.ADMIN).isActive(true).build());

        userRepository.save(User.builder()
                .name("Analyst User").email("analyst@fin.dev")
                .password(encoded).role(Role.ANALYST).isActive(true).build());

        userRepository.save(User.builder()
                .name("Viewer User").email("viewer@fin.dev")
                .password(encoded).role(Role.VIEWER).isActive(true).build());

        // Helper
        record R(int amount, RecordType type, String cat, int y, int m, int d, String notes) {}
        R[] recs = {
            new R(85000, RecordType.INCOME, "Salary",      2025, 11,  1, "Monthly salary Nov"),
            new R(85000, RecordType.INCOME, "Salary",      2025, 12,  1, "Monthly salary Dec"),
            new R(85000, RecordType.INCOME, "Salary",      2026,  1,  1, "Monthly salary Jan"),
            new R(85000, RecordType.INCOME, "Salary",      2026,  2,  1, "Monthly salary Feb"),
            new R(85000, RecordType.INCOME, "Salary",      2026,  3,  1, "Monthly salary Mar"),
            new R(15000, RecordType.INCOME, "Freelance",   2025, 11, 15, "Web design project"),
            new R(22000, RecordType.INCOME, "Freelance",   2025, 12, 20, "App development contract"),
            new R( 8000, RecordType.INCOME, "Freelance",   2026,  1, 18, "Logo design gig"),
            new R(  500, RecordType.INCOME, "Interest",    2025, 11, 30, "Savings account"),
            new R(  500, RecordType.INCOME, "Interest",    2025, 12, 31, "Savings account"),
            new R(  500, RecordType.INCOME, "Interest",    2026,  1, 31, "Savings account"),
            new R(  500, RecordType.INCOME, "Interest",    2026,  2, 28, "Savings account"),
            new R(18000, RecordType.EXPENSE, "Rent",       2025, 11,  5, "Monthly rent Nov"),
            new R(18000, RecordType.EXPENSE, "Rent",       2025, 12,  5, "Monthly rent Dec"),
            new R(18000, RecordType.EXPENSE, "Rent",       2026,  1,  5, "Monthly rent Jan"),
            new R(18000, RecordType.EXPENSE, "Rent",       2026,  2,  5, "Monthly rent Feb"),
            new R(18000, RecordType.EXPENSE, "Rent",       2026,  3,  5, "Monthly rent Mar"),
            new R( 6200, RecordType.EXPENSE, "Groceries",  2025, 11, 10, "Monthly groceries"),
            new R( 7100, RecordType.EXPENSE, "Groceries",  2025, 12, 12, "December groceries"),
            new R( 5800, RecordType.EXPENSE, "Groceries",  2026,  1,  8, "January groceries"),
            new R( 6500, RecordType.EXPENSE, "Groceries",  2026,  2, 11, "February groceries"),
            new R( 3200, RecordType.EXPENSE, "Utilities",  2025, 11, 15, "Electricity + internet"),
            new R( 2900, RecordType.EXPENSE, "Utilities",  2025, 12, 15, "Utilities Dec"),
            new R( 3100, RecordType.EXPENSE, "Utilities",  2026,  1, 15, "Utilities Jan"),
            new R( 2800, RecordType.EXPENSE, "Utilities",  2026,  2, 15, "Utilities Feb"),
            new R( 4500, RecordType.EXPENSE, "Entertainment", 2025, 11, 20, "Movies and dining"),
            new R( 8900, RecordType.EXPENSE, "Entertainment", 2025, 12, 25, "Christmas celebrations"),
            new R( 2100, RecordType.EXPENSE, "Entertainment", 2026,  1, 22, "Entertainment Jan"),
            new R( 1200, RecordType.EXPENSE, "Healthcare", 2026,  2, 10, "Doctor visit"),
            new R( 5500, RecordType.EXPENSE, "Transport",  2025, 12,  3, "Car maintenance"),
            new R( 1800, RecordType.EXPENSE, "Transport",  2026,  1, 10, "Fuel and commute"),
            new R(12000, RecordType.EXPENSE, "Education",  2026,  1,  5, "Online course subscription"),
        };

        for (R r : recs) {
            recordRepository.save(FinancialRecord.builder()
                    .amount(new BigDecimal(r.amount()))
                    .type(r.type())
                    .category(r.cat())
                    .date(LocalDate.of(r.y(), r.m(), r.d()))
                    .notes(r.notes())
                    .createdBy(admin)
                    .isDeleted(false)
                    .build());
        }

        log.info("Seeding complete! {} records inserted.", recs.length);
    }
}
