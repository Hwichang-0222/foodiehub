package org.embed.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class DebugController {

    /* ============================================
       디버깅용 - 현재 로그인 상태 확인
    ============================================ */
    
    @GetMapping("/debug/auth")
    @ResponseBody
    public Map<String, Object> checkAuthStatus(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        // 세션 확인
        Object sessionUser = session.getAttribute("user");
        result.put("세션_사용자", sessionUser != null ? sessionUser.toString() : "없음");
        
        // Spring Security 인증 정보 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            result.put("인증_여부", auth.isAuthenticated());
            result.put("사용자명", auth.getName());
            result.put("권한_목록", auth.getAuthorities().toString());
            result.put("인증_타입", auth.getClass().getSimpleName());
        } else {
            result.put("Spring_Security_인증", "없음");
        }
        
        return result;
    }
}