package com.splitledger.app.service;

import com.splitledger.app.entity.GroupMember;
import com.splitledger.app.entity.IncomeRecord;
import com.splitledger.app.entity.PayoutSplit;
import com.splitledger.app.repository.GroupMemberRepository;
import com.splitledger.app.repository.IncomeRecordRepository;
import com.splitledger.app.repository.PayoutSplitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncomeService {

    private final IncomeRecordRepository incomeRecordRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PayoutSplitRepository payoutSplitRepository;
    private final SplitCalculatorService splitCalculatorService;

    public IncomeService(IncomeRecordRepository incomeRecordRepository,
                         GroupMemberRepository groupMemberRepository,
                         PayoutSplitRepository payoutSplitRepository,
                         SplitCalculatorService splitCalculatorService) {
        this.incomeRecordRepository = incomeRecordRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.payoutSplitRepository = payoutSplitRepository;
        this.splitCalculatorService = splitCalculatorService;
    }

    @Transactional
    public IncomeRecord registerIncome(String sourceName, BigDecimal grossAmount, BigDecimal deductions, LocalDate receivedDate) {
        if (deductions == null) deductions = BigDecimal.ZERO;
        BigDecimal netDistributable = grossAmount.subtract(deductions);

        IncomeRecord record = new IncomeRecord(sourceName, grossAmount, deductions, netDistributable, receivedDate);
        IncomeRecord savedRecord = incomeRecordRepository.save(record);

        List<GroupMember> activeMembers = groupMemberRepository.findByActiveTrue();
        List<PayoutSplit> splits = splitCalculatorService.calculateSplits(savedRecord, activeMembers);

        payoutSplitRepository.saveAll(splits);
        return savedRecord;
    }

    @Transactional
    public void markAsSent(Long payoutSplitId, String transactionRef) {
        PayoutSplit split = payoutSplitRepository.findById(payoutSplitId)
                .orElseThrow(() -> new IllegalArgumentException("Payout split not found: " + payoutSplitId));
        split.setPaymentStatus(PayoutSplit.Status.SENT);
        split.setTransactionRef(transactionRef);
        payoutSplitRepository.save(split);
    }

    @Transactional
    public void confirmReceived(Long payoutSplitId) {
        PayoutSplit split = payoutSplitRepository.findById(payoutSplitId)
                .orElseThrow(() -> new IllegalArgumentException("Payout split not found: " + payoutSplitId));
        split.setPaymentStatus(PayoutSplit.Status.SETTLED);
        split.setSettledAt(LocalDateTime.now());
        payoutSplitRepository.save(split);
    }
}