package com.example.jwt.filter;

import com.example.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ==========================================
        // 1. Lấy Authorization Header
        // ==========================================

        final String authHeader =
                request.getHeader("Authorization");

        // Không có Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // 2. Lấy JWT
        // ==========================================

        final String jwt = authHeader.substring(7);

        // ==========================================
        // 3. Lấy username từ JWT
        // ==========================================

        final String username;

        try {

            username = jwtService.extractUsername(jwt);

        } catch (Exception e) {

            // JWT không hợp lệ
            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // 4. Kiểm tra SecurityContext
        // ==========================================

        if (username != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            // ==========================================
            // 5. Tìm User trong database
            // ==========================================

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // ==========================================
            // 6. Kiểm tra JWT
            // ==========================================

            if (jwtService.isTokenValid(
                    jwt,
                    userDetails
            )) {

                // ==========================================
                // 7. Tạo Authentication
                // ==========================================

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // ==========================================
                // 8. Đưa Authentication vào SecurityContext
                // ==========================================

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // ==========================================
        // 9. Cho request đi tiếp
        // ==========================================

        filterChain.doFilter(request, response);
    }
}
