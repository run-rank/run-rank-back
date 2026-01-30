package com.example.runnig_back.service;

import com.example.runnig_back.dto.request.AuthRequest;
import com.example.runnig_back.dto.request.LoginRequest;
import com.example.runnig_back.dto.response.AuthResponse;
import com.example.runnig_back.entity.User;
import com.example.runnig_back.exception.CustomException;
import com.example.runnig_back.exception.ErrorCode;
import com.example.runnig_back.repository.UserRepository;
import com.example.runnig_back.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void signup(@Valid AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_INPUT_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_INPUT_INVALID);
        }

        String accessToken = jwtProvider.createToken(user.getEmail());
        return new AuthResponse(accessToken);
    }
}
