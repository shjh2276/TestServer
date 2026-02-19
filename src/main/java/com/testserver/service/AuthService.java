package com.testserver.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    // 테스트용 임시 계정 저장소 (실제 환경에서는 DB 사용)
    private static final Map<String, String> USER_STORE = new HashMap<>();

    static {
        USER_STORE.put("admin", "1234");
        USER_STORE.put("user1", "pass1");
    }

    public String login(String username, String password) {
        if (!USER_STORE.containsKey(username)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!USER_STORE.get(username).equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return username;
    }
}
