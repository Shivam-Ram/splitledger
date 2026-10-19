package com.splitledger.app.config;

import com.splitledger.app.entity.GroupMember;
import com.splitledger.app.entity.User;
import com.splitledger.app.repository.GroupMemberRepository;
import com.splitledger.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           GroupMemberRepository groupMemberRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Admin password: admin123
            User admin = new User("Shivam (Admin)", "admin@ledger.com", passwordEncoder.encode("admin123"), User.Role.ADMIN, "shivam@upi");
            userRepository.save(admin);

            // Members password: pass123
            User personA = new User("Rahul Sharma", "rahul@ledger.com", passwordEncoder.encode("pass123"), User.Role.MEMBER, "rahul@okaxis");
            User personB = new User("Pooja Verma", "pooja@ledger.com", passwordEncoder.encode("pass123"), User.Role.MEMBER, "pooja@oksbi");
            User personC = new User("Amit Singh", "amit@ledger.com", passwordEncoder.encode("pass123"), User.Role.MEMBER, "amit@okhdfc");
            User personD = new User("Neha Patel", "neha@ledger.com", passwordEncoder.encode("pass123"), User.Role.MEMBER, "neha@paytm");

            userRepository.save(personA);
            userRepository.save(personB);
            userRepository.save(personC);
            userRepository.save(personD);

            groupMemberRepository.save(new GroupMember(personA, new BigDecimal("40.00")));
            groupMemberRepository.save(new GroupMember(personB, new BigDecimal("30.00")));
            groupMemberRepository.save(new GroupMember(personC, new BigDecimal("15.00")));
            groupMemberRepository.save(new GroupMember(personD, new BigDecimal("15.00")));

            System.out.println(">>> Seed users created with encrypted passwords! <<<");
        }
    }
}