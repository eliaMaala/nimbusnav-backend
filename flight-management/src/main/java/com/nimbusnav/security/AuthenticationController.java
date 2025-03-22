package com.nimbusnav.security;


import com.nimbusnav.dto.LoginRequest;
import com.nimbusnav.dto.RegisterRequest;
import com.nimbusnav.flightmanagement.models.RevokedToken;
import com.nimbusnav.flightmanagement.repositories.RevokedTokenRepository;
import com.nimbusnav.flightmanagement.repositories.UserRepository;
//import com.nimbusnav.flightmanagement.services.AuthService;
import com.nimbusnav.flightmanagement.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final RevokedTokenRepository revokedTokenRepository;

    public AuthenticationController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthService authService, RevokedTokenRepository revokedTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    //  قائمة لحظر `Refresh Tokens` عند تسجيل الخروج
    private final Map<String,Boolean> tokenBlackList = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        String response = authService. registerUser(request);
        return ResponseEntity.ok(response) ;
    }

    //  تسجيل الدخول وإرجاع `Access Token` و `Refresh Token`
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.loginUser(request));
    }

    //  تجديد `Access Token` باستخدام `Refresh Token`
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestParam String refreshToken){
        if (revokedTokenRepository.existsByToken(refreshToken)){
            return ResponseEntity.status(403).body("Invalid Refresh Token: Token is blacklisted");
        }
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(newAccessToken);
        }catch (Exception e){
            return ResponseEntity.status(403).body("Invalid Refresh Token");
        }
    }

    //  تسجيل الخروج (حظر `Refresh Token`)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken){
        tokenBlackList.put(refreshToken,true);
        revokedTokenRepository.save(new RevokedToken(refreshToken));
        return ResponseEntity.ok("Logged out successfully.");
    }


}
