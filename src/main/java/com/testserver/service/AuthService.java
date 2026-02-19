package com.testserver.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    // 테스트용 임시 계정 저장소 (실제 환경에서는 DB 사용)
    private static final Map<String, String> USER_STORE = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        // 초기 테스트 계정 (BCrypt 암호화 저장)
        USER_STORE.put("admin", passwordEncoder.encode("1234"));
        USER_STORE.put("user1", passwordEncoder.encode("pass1"));
    }

    public String login(String username, String password) {
        if (!USER_STORE.containsKey(username)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!passwordEncoder.matches(password, USER_STORE.get(username))) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return username;
    }

    public String signup(String username, String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (USER_STORE.containsKey(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        USER_STORE.put(username, passwordEncoder.encode(password));
        return username;
    }
}
