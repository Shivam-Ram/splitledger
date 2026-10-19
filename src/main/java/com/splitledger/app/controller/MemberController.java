package com.splitledger.app.controller;

import com.splitledger.app.entity.PayoutSplit;
import com.splitledger.app.entity.User;
import com.splitledger.app.repository.PayoutSplitRepository;
import com.splitledger.app.repository.UserRepository;
import com.splitledger.app.service.IncomeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final PayoutSplitRepository payoutSplitRepository;
    private final UserRepository userRepository;
    private final IncomeService incomeService;

    public MemberController(PayoutSplitRepository payoutSplitRepository,
                            UserRepository userRepository,
                            IncomeService incomeService) {
        this.payoutSplitRepository = payoutSplitRepository;
        this.userRepository = userRepository;
        this.incomeService = incomeService;
    }

    @GetMapping("/dashboard")
    public String showMemberDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userDetails.getUsername()));

        List<PayoutSplit> myPayouts = payoutSplitRepository.findByUserIdOrderByIncomeRecordReceivedDateDesc(currentUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myPayouts", myPayouts);

        PayoutSplit pendingAction = myPayouts.stream()
                .filter(p -> p.getPaymentStatus() == PayoutSplit.Status.SENT)
                .findFirst()
                .orElse(null);
        model.addAttribute("pendingAction", pendingAction);

        return "member/dashboard";
    }

    @PostMapping("/confirm-received")
    public String confirmReceived(@RequestParam Long splitId) {
        incomeService.confirmReceived(splitId);
        return "redirect:/member/dashboard";
    }
}