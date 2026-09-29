package com.example.jwt.config;

import com.example.jwt.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // ==========================================
                // Bật CORS để cho phép frontend gọi API
                // ==========================================
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ==========================================
                // Tắt CSRF vì sử dụng JWT
                // ==========================================
                .csrf(csrf -> csrf.disable())

                // ==========================================
                // JWT -> Stateless
                // ==========================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==========================================
                // Cấu hình xử lý Exception rõ ràng (401 và 403)
                // ==========================================
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Chưa xác thực hoặc token không hợp lệ\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Bạn không có quyền truy cập tài nguyên này\"}");
                        })
                )

                // ==========================================
                // Authentication Provider
                // ==========================================
                .authenticationProvider(
                        authenticationProvider
                )

                // ==========================================
                // JWT Filter
                // ==========================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // ==========================================
                // PHÂN QUYỀN
                // ==========================================
                .authorizeHttpRequests(auth -> auth

                        // LOGIN + REGISTER
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register"
                        ).permitAll()

                        // FRONTEND & TÀI NGUYÊN TĨNH
                        .requestMatchers(
                                "/",
                                "/login.html",
                                "/register.html",
                                "/profile.html",
                                "/js/**",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // USER + ADMIN
                        .requestMatchers("/users/**")
                        .hasAnyRole("USER", "ADMIN")

                        // ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // CÒN LẠI
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // ==========================================
    // Cấu hình CORS chi tiết
    // ==========================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
