package com.example.jwt.controller;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.dto.LoginResponse;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.entity.User;
import com.example.jwt.service.JwtService;
import com.example.jwt.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // ==========================================
    // REGISTER
    // POST /auth/register
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody RegisterRequest request
    ) {

        User user = userService.register(request);

        // Không trả password về client
        user.setPassword(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    // ==========================================
    // LOGIN
    // POST /auth/login
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        // Xác thực username + password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Lấy UserDetails
        UserDetails userDetails =
                userService.loadUserByUsername(
                        request.getUsername()
                );

        // Tạo JWT
        String token =
                jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}
