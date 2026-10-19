package com.splitledger.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "income_records")
public class IncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_name", nullable = false, length = 150)
    private String sourceName;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "deductions", precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "net_distributable", nullable = false, precision = 12, scale = 2)
    private BigDecimal netDistributable;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public IncomeRecord() {}

    public IncomeRecord(String sourceName, BigDecimal grossAmount, BigDecimal deductions, BigDecimal netDistributable, LocalDate receivedDate) {
        this.sourceName = sourceName;
        this.grossAmount = grossAmount;
        this.deductions = deductions;
        this.netDistributable = netDistributable;
        this.receivedDate = receivedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public BigDecimal getGrossAmount() { return grossAmount; }
    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }

    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getNetDistributable() { return netDistributable; }
    public void setNetDistributable(BigDecimal netDistributable) { this.netDistributable = netDistributable; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}