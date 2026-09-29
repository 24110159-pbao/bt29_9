package com.example.jwt.service;

import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        } else {
            role = role.trim().toUpperCase();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
        }
        user.setRole(role);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy user"
                        )
                );
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Không tìm thấy username: " + username
                        )
                );

        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        } else {
            role = role.trim().toUpperCase();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}
