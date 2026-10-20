package com.splitledger.app.controller;

import com.splitledger.app.entity.User;
import com.splitledger.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Pattern;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Strict email regex pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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

        // 1. Email format check
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            model.addAttribute("error", "Please provide a valid email address (e.g. user@gmail.com)");
            return "auth/register";
        }

        // 2. Password length check
        if (password == null || password.trim().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long.");
            return "auth/register";
        }

        // 3. Email uniqueness check
        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            model.addAttribute("error", "Email is already registered! Please sign in.");
            return "auth/register";
        }

        // 4. Create and Save user
        User newUser = new User(
            name.trim(), 
            email.trim().toLowerCase(), 
            passwordEncoder.encode(password), 
            User.Role.MEMBER, 
            (upiId != null && !upiId.isBlank()) ? upiId.trim() : null
        );
        userRepository.save(newUser);

        return "redirect:/login?registered=true";
    }
}