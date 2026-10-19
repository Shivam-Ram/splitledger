package com.splitledger.app.repository;

import com.splitledger.app.entity.IncomeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeRecordRepository extends JpaRepository<IncomeRecord, Long> {
    List<IncomeRecord> findAllByOrderByReceivedDateDesc();
}