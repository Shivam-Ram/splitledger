package com.splitledger.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_splits")
public class PayoutSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_record_id", nullable = false)
    private IncomeRecord incomeRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "percentage_applied", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageApplied;

    @Column(name = "calculated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal calculatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private Status paymentStatus = Status.PENDING;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    public enum Status {
        PENDING, SENT, SETTLED
    }

    public PayoutSplit() {}

    public PayoutSplit(IncomeRecord incomeRecord, User user, BigDecimal percentageApplied, BigDecimal calculatedAmount) {
        this.incomeRecord = incomeRecord;
        this.user = user;
        this.percentageApplied = percentageApplied;
        this.calculatedAmount = calculatedAmount;
        this.paymentStatus = Status.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IncomeRecord getIncomeRecord() { return incomeRecord; }
    public void setIncomeRecord(IncomeRecord incomeRecord) { this.incomeRecord = incomeRecord; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getPercentageApplied() { return percentageApplied; }
    public void setPercentageApplied(BigDecimal percentageApplied) { this.percentageApplied = percentageApplied; }

    public BigDecimal getCalculatedAmount() { return calculatedAmount; }
    public void setCalculatedAmount(BigDecimal calculatedAmount) { this.calculatedAmount = calculatedAmount; }

    public Status getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Status paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }
}