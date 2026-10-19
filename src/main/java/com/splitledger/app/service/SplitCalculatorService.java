package com.splitledger.app.service;

import com.splitledger.app.entity.GroupMember;
import com.splitledger.app.entity.IncomeRecord;
import com.splitledger.app.entity.PayoutSplit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class SplitCalculatorService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    public List<PayoutSplit> calculateSplits(IncomeRecord incomeRecord, List<GroupMember> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate splits without active members.");
        }

        // Validate percentage sum equals 100.00%
        BigDecimal totalPercentage = members.stream()
                .map(GroupMember::getPercentageShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(ONE_HUNDRED) != 0) {
            throw new IllegalStateException("Configured shares total " + totalPercentage + "%. They must sum to exactly 100.00%.");
        }

        BigDecimal netPool = incomeRecord.getNetDistributable();
        List<PayoutSplit> splits = new ArrayList<>();
        BigDecimal totalCalculated = BigDecimal.ZERO;

        for (GroupMember member : members) {
            // formula: (netPool * percentage) / 100
            BigDecimal shareAmount = netPool.multiply(member.getPercentageShare())
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

            PayoutSplit split = new PayoutSplit(incomeRecord, member.getUser(), member.getPercentageShare(), shareAmount);
            splits.add(split);
            totalCalculated = totalCalculated.add(shareAmount);
        }

        // Handle fractional rounding differences (odd paise/cents)
        BigDecimal remainder = netPool.subtract(totalCalculated);
        if (remainder.compareTo(BigDecimal.ZERO) != 0 && !splits.isEmpty()) {
            PayoutSplit firstEntry = splits.get(0);
            firstEntry.setCalculatedAmount(firstEntry.getCalculatedAmount().add(remainder));
        }

        return splits;
    }
}