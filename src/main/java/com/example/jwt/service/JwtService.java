package com.example.jwt.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secretkey}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    // ==========================================
    // Lấy secret bytes phục vụ ký và xác thực
    // ==========================================
    private byte[] getSigningKeyBytes() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    // ==========================================
    // 1. Tạo JWT bằng Nimbus
    // ==========================================
    public String generateToken(UserDetails userDetails) {
        try {
            // Tạo Header thuật toán HMAC SHA-256
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            // Tạo Claims
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(expiryDate)
                    .build();

            // Đóng gói JWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Ký token với MACSigner (Nimbus)
            JWSSigner signer = new MACSigner(getSigningKeyBytes());
            signedJWT.sign(signer);

            // Tuần tự hóa sang chuỗi ký tự
            return signedJWT.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo JWT với Nimbus: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 2. Trích xuất username (Subject) từ JWT
    // ==========================================
    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi trích xuất username từ JWT: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 3. Kiểm tra tính hợp lệ của Token
    // ==========================================
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Xác minh chữ ký với MACVerifier
            JWSVerifier verifier = new MACVerifier(getSigningKeyBytes());
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // Kiểm tra subject và hạn sử dụng
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String username = claimsSet.getSubject();
            Date expirationTime = claimsSet.getExpirationTime();

            return username != null
                    && username.equals(userDetails.getUsername())
                    && expirationTime != null
                    && expirationTime.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}
