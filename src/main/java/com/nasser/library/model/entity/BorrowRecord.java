package com.nasser.library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "borrow_records")
public class BorrowRecord extends BaseEntity {

    @NotNull(message = "Borrow date is required")
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @NotNull(message = "Borrow status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BorrowStatus status = BorrowStatus.ACTIVE;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Size(max = 500, message = "Return condition notes must not exceed 500 characters")
    @Column(name = "return_condition_notes", length = 500)
    private String returnConditionNotes;

    @DecimalMin(value = "0.0", message = "Fine amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Fine amount format is invalid")
    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "is_fine_paid")
    private Boolean isFinePaid = false;

    @Column(name = "checked_out_by", length = 100)
    private String checkedOutBy; // Librarian who processed the checkout

    @Column(name = "checked_in_by", length = 100)
    private String checkedInBy; // Librarian who processed the return

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Fine> fines = new HashSet<>();

    // Business logic methods
    public boolean isOverdue() {
        return this.status == BorrowStatus.ACTIVE && 
               this.dueDate.isBefore(LocalDate.now());
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
    }

    public void markAsReturned(String checkedInBy) {
        this.status = BorrowStatus.RETURNED;
        this.returnDate = LocalDate.now();
        this.checkedInBy = checkedInBy;
    }

    public void markAsLost() {
        this.status = BorrowStatus.LOST;
    }

    public void addFine(BigDecimal amount, String reason) {
        this.fineAmount = this.fineAmount.add(amount);
        this.fines.add(new Fine(this, amount, reason));
    }

    public void payFine() {
        this.isFinePaid = true;
    }


    // Calculate fine based on overdue days
    public BigDecimal calculateOverdueFine(BigDecimal dailyFineRate) {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        long overdueDays = getDaysOverdue();
        return dailyFineRate.multiply(BigDecimal.valueOf(overdueDays));
    }

    @PrePersist
    private void prePersist() {
        if (this.borrowDate == null) {
            this.borrowDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            // Default to 14 days from borrow date
            this.dueDate = this.borrowDate.plusDays(14);
        }
    }
} 