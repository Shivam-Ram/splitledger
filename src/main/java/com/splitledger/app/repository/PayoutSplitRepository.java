package com.splitledger.app.repository;

import com.splitledger.app.entity.PayoutSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayoutSplitRepository extends JpaRepository<PayoutSplit, Long> {
    List<PayoutSplit> findByIncomeRecordId(Long incomeRecordId);
    List<PayoutSplit> findByUserIdOrderByIncomeRecordReceivedDateDesc(Long userId);
}