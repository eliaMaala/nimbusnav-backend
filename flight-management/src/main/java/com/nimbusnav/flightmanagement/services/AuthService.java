package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.Enum.Role;
import com.nimbusnav.dto.LoginRequest;
import com.nimbusnav.dto.RegisterRequest;
import com.nimbusnav.flightmanagement.models.UserEntity;
import com.nimbusnav.flightmanagement.repositories.UserRepository;
import com.nimbusnav.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(RegisterRequest request) {
        // تحقق إذا كان المستخدم موجودًا مسبقًا
        Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
        Optional<UserEntity> existingUserEmail = userRepository.findByUsername(request.getEmail());

        if(existingUser.isPresent() || existingUserEmail.isPresent()) {
            return "User already exists. Please log in instead.";
        }


        Role role = Role.fromString(request.getRole());
        System.out.println("Parsed role: " + role);

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully. Please log in to get your token.";
    }

    // ✅ تسجيل الدخول وإرجاع Access Token و Refresh Token
    public Map<String ,String> loginUser(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String accessToken = jwtUtil.generateACCESSToken(user.getUsername(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId(), user.getRole().name());

        Map<String,String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken",refreshToken);

        return tokens;
    }

    public String refreshAccessToken (String refreshToken){
        String username = jwtUtil.extractUsername(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);
        UUID userId = jwtUtil.extractUserId(refreshToken);

        if (!jwtUtil.isTokenValid(refreshToken,username)){
            throw new RuntimeException("Invalid Refresh Token");
        }
        return jwtUtil.generateACCESSToken(username,userId,role);
    }
}
