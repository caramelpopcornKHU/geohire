package com.geohire.service;

import com.geohire.config.JwtTokenProvider;
import com.geohire.dto.AuthResponse;
import com.geohire.dto.LoginRequest;
import com.geohire.dto.SignupRequest;
import com.geohire.entity.*;
import com.geohire.repository.CompanyProfileRepository;
import com.geohire.repository.SeekerProfileRepository;
import com.geohire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        // 역할에 따라 프로필 생성
        if (request.getRole() == Role.SEEKER) {
            SeekerProfile profile = SeekerProfile.builder()
                    .user(user)
                    .build();
            seekerProfileRepository.save(profile);
        } else if (request.getRole() == Role.COMPANY) {
            CompanyProfile profile = CompanyProfile.builder()
                    .user(user)
                    .companyName(request.getCompanyName() != null ? request.getCompanyName() : request.getName())
                    .build();
            companyProfileRepository.save(profile);
        }

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
