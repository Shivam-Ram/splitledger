package com.splitledger.app.controller;

import com.splitledger.app.entity.GroupMember;
import com.splitledger.app.entity.IncomeRecord;
import com.splitledger.app.entity.PayoutSplit;
import com.splitledger.app.entity.User;
import com.splitledger.app.repository.GroupMemberRepository;
import com.splitledger.app.repository.IncomeRecordRepository;
import com.splitledger.app.repository.PayoutSplitRepository;
import com.splitledger.app.repository.UserRepository;
import com.splitledger.app.service.IncomeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IncomeService incomeService;
    private final IncomeRecordRepository incomeRecordRepository;
    private final PayoutSplitRepository payoutSplitRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(IncomeService incomeService,
                           IncomeRecordRepository incomeRecordRepository,
                           PayoutSplitRepository payoutSplitRepository,
                           GroupMemberRepository groupMemberRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.incomeService = incomeService;
        this.incomeRecordRepository = incomeRecordRepository;
        this.payoutSplitRepository = payoutSplitRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Dashboard View
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<IncomeRecord> records = incomeRecordRepository.findAllByOrderByReceivedDateDesc();
        model.addAttribute("incomeRecords", records);

        if (!records.isEmpty()) {
            IncomeRecord latest = records.get(0);
            List<PayoutSplit> splits = payoutSplitRepository.findByIncomeRecordId(latest.getId());
            model.addAttribute("latestRecord", latest);
            model.addAttribute("splits", splits);
        }

        return "admin/dashboard";
    }

    // 2. Add Income Event
    @PostMapping("/add-income")
    public String addIncome(@RequestParam String sourceName,
                            @RequestParam BigDecimal grossAmount,
                            @RequestParam(required = false, defaultValue = "0") BigDecimal deductions,
                            @RequestParam String receivedDate) {
        incomeService.registerIncome(sourceName, grossAmount, deductions, LocalDate.parse(receivedDate));
        return "redirect:/admin/dashboard";
    }

    // 3. Mark Payout as Sent
    @PostMapping("/mark-sent")
    public String markSent(@RequestParam Long splitId,
                           @RequestParam(required = false) String transactionRef) {
        incomeService.markAsSent(splitId, transactionRef);
        return "redirect:/admin/dashboard";
    }

    // 4. Manage Members Page
    @GetMapping("/members")
    public String showMembers(Model model, @RequestParam(required = false) String error) {
        List<GroupMember> members = groupMemberRepository.findByActiveTrue();
        BigDecimal totalPercentage = members.stream()
                .map(GroupMember::getPercentageShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("members", members);
        model.addAttribute("totalPercentage", totalPercentage);
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        return "admin/manage-members";
    }

    // 5. Add New Member with Share
    @PostMapping("/members/add")
    public String addMember(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String upiId,
                            @RequestParam BigDecimal percentageShare) {
        // Password hashed using BCrypt
        User newUser = new User(name, email, passwordEncoder.encode("pass123"), User.Role.MEMBER, upiId);
        userRepository.save(newUser);

        GroupMember member = new GroupMember(newUser, percentageShare);
        groupMemberRepository.save(member);

        return "redirect:/admin/members";
    }

    // 6. Delete / Remove Member
    @PostMapping("/members/delete")
    public String deleteMember(@RequestParam Long memberId) {
        groupMemberRepository.deleteById(memberId);
        return "redirect:/admin/members";
    }

    // 7. Update All Percentage Shares
    @PostMapping("/members/update-shares")
    public String updateShares(@RequestParam Map<String, String> allParams) {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("share_")) {
                BigDecimal share = new BigDecimal(entry.getValue());
                total = total.add(share);
            }
        }

        // Strict validation: Total must equal 100.00%
        if (total.compareTo(new BigDecimal("100.00")) != 0) {
            return "redirect:/admin/members?error=Total+shares+must+equal+exactly+100.00%25+(Current+Total:+" + total + "%25)";
        }

        // Save updated percentages
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("share_")) {
                Long memberId = Long.parseLong(entry.getKey().replace("share_", ""));
                BigDecimal share = new BigDecimal(entry.getValue());
                groupMemberRepository.findById(memberId).ifPresent(m -> {
                    m.setPercentageShare(share);
                    groupMemberRepository.save(m);
                });
            }
        }

        return "redirect:/admin/members";
    }
}