package com.fitpro.service;

import com.fitpro.domain.entity.RefreshToken;
import com.fitpro.domain.entity.User;
import com.fitpro.domain.repository.RefreshTokenRepository;
import com.fitpro.domain.repository.UserRepository;
import com.fitpro.dto.auth.AuthResponse;
import com.fitpro.dto.auth.LoginRequest;
import com.fitpro.dto.auth.UserProfileDto;
import com.fitpro.exception.BusinessException;
import com.fitpro.security.JwtService;
import com.fitpro.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setLastLoginAt(Instant.now());
        user.setLastLoginIp(httpRequest.getRemoteAddr());
        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        saveRefreshToken(user.getId(), refreshToken, httpRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .user(toProfile(user))
                .build();
    }

    @Transactional
    public AuthResponse refresh(String refreshToken, HttpServletRequest httpRequest) {
        String hash = hashToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expired");
        }

        User user = userRepository.findByEmailWithRoles(
                userRepository.findById(stored.getUserId())
                        .orElseThrow(() -> new BusinessException("User not found"))
                        .getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        UserPrincipal principal = new UserPrincipal(user);
        String newAccessToken = jwtService.generateAccessToken(principal);
        String newRefreshToken = jwtService.generateRefreshToken(principal);

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        saveRefreshToken(user.getId(), newRefreshToken, httpRequest);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .user(toProfile(user))
                .build();
    }

    public UserProfileDto me(UserPrincipal principal) {
        User user = userRepository.findByEmailWithRoles(principal.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));
        return toProfile(user);
    }

    private void saveRefreshToken(UUID userId, String token, HttpServletRequest request) {
        RefreshToken rt = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashToken(token))
                .expiresAt(Instant.now().plusMillis(604800000))
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();
        refreshTokenRepository.save(rt);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new BusinessException("Token hashing failed");
        }
    }

    private UserProfileDto toProfile(User user) {
        List<String> roles = user.getRoles().stream().map(r -> r.getName()).toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .distinct()
                .toList();

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .gymId(user.getGymId())
                .branchId(user.getBranchId())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
