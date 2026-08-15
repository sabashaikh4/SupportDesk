package com.supportdesk.service;

import com.supportdesk.entity.Role;
import com.supportdesk.entity.User;
import com.supportdesk.repository.UserRepository;
import com.supportdesk.request.LoginRequest;
import com.supportdesk.request.RegisterRequest;
import com.supportdesk.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailsService;

    // ─────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1. check email not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // 2. create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));

        // 3. save to DB
        userRepository.save(user);

        // 4. generate token
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        // 5. return response
        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(Role.ROLE_USER.name())
                .build();
    }

    // ─────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        // 1. find user by email
        User user = userRepository
                .findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. generate token
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        // 4. return response
        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRoles().iterator().next().name())
                .build();
    }
}