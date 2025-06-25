package com.nasser.library.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "fines")
public class Fine extends BaseEntity {

    @NotNull(message = "Fine amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fine amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Fine amount format is invalid")
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Fine type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private FineType type;

    @NotNull(message = "Fine status is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private FineStatus status = FineStatus.PENDING;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;


    @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Paid amount format is invalid")
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Size(max = 100, message = "Processed by must not exceed 100 characters")
    @Column(name = "processed_by", length = 100)
    private String processedBy;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id")
    private BorrowRecord borrowRecord;

    public Fine(BorrowRecord borrowRecord, BigDecimal amount, String reason) {
        super();
        this.borrowRecord = borrowRecord;
        this.user = borrowRecord.getUser();
        this.amount = amount;
        this.reason = reason;
    }

    // Business logic methods
    public boolean isPending() {
        return this.status == FineStatus.PENDING;
    }

    public boolean isPaid() {
        return this.status == FineStatus.PAID;
    }


    public boolean isOverdue() {
        return this.status == FineStatus.PENDING &&
                this.dueDate != null &&
                this.dueDate.isBefore(LocalDate.now());
    }

    public BigDecimal getRemainingAmount() {
        return this.amount.subtract(this.paidAmount);
    }

    public boolean isFullyPaid() {
        return this.paidAmount.compareTo(this.amount) >= 0;
    }

    public void markAsPaid(BigDecimal paidAmount, String paymentMethod, String paymentReference, String processedBy) {
        this.paidAmount = this.paidAmount.add(paidAmount);
        this.processedBy = processedBy;

        this.status = FineStatus.PAID;
        this.paidDate = LocalDate.now();
    }


    @PrePersist
    private void prePersist() {
        if (this.issueDate == null) {
            this.issueDate = LocalDate.now();
        }
        if (this.dueDate == null && this.type == FineType.OVERDUE) {
            // Default due date for fine payment is 30 days from issue date
            this.dueDate = this.issueDate.plusDays(30);
        }
    }
} 