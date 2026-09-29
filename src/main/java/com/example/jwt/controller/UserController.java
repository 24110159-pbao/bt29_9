package com.example.jwt.controller;

import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // GET CURRENT USER
    // GET /users/me
    // ==========================================

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            Authentication authentication
    ) {

        String username = authentication.getName();

        User user = userService.findByUsername(username);

        // Không trả password
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    // ==========================================
    // TEST PROTECTED API
    // GET /users
    // ==========================================

    @GetMapping
    public ResponseEntity<String> getUsers(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                "Xin chào " +
                        authentication.getName() +
                        ", bạn đã xác thực JWT thành công!"
        );
    }
}
