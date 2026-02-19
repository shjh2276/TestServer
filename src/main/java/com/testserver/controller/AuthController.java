package com.testserver.controller;

import com.testserver.dto.ApiResponse;
import com.testserver.dto.LoginRequest;
import com.testserver.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_KEY = "LOGIN_USER";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpSession session) {

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMsg));
        }

        // 이미 로그인된 경우
        if (session.getAttribute(SESSION_KEY) != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("이미 로그인된 상태입니다."));
        }

        try {
            String username = authService.login(request.getUsername(), request.getPassword());
            session.setAttribute(SESSION_KEY, username);
            return ResponseEntity.ok(ApiResponse.ok("로그인 성공", Map.of("username", username)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpSession session) {
        if (session.getAttribute(SESSION_KEY) == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("로그인 상태가 아닙니다."));
        }
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 성공", null));
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(HttpSession session) {
        String username = (String) session.getAttribute(SESSION_KEY);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok("인증 확인", Map.of("username", username)));
    }
}
