package com.splitledger.app.controller;

import com.splitledger.app.entity.User;
import com.splitledger.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String upiId,
                               Model model) {
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "auth/register";
        }

        User newUser = new User(name, email, passwordEncoder.encode(password), User.Role.MEMBER, upiId);
        userRepository.save(newUser);

        return "redirect:/login?registered=true";
    }
}