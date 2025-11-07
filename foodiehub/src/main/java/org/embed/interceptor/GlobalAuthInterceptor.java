package org.embed.interceptor;

import org.embed.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GlobalAuthInterceptor implements HandlerInterceptor {

    /* ============================================
       전역 권한 검증 Interceptor
    ============================================ */
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");
        
        /* ============================================
           관리자 페이지 (/admin/**)
        ============================================ */
        if (requestURI.startsWith("/admin")) {
            // 로그인 안 됨 → 로그인 페이지로 리다이렉트
            if (user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
            
            // 관리자 권한 없음 → AccessDeniedException 던지기 (GlobalExceptionHandler가 처리)
            if (!"ROLE_ADMIN".equals(user.getRole())) {
                throw new AccessDeniedException("관리자 권한이 필요합니다");
            }
        }
        
        /* ============================================
           마이페이지, 회원정보 수정 (/user/mypage, /user/edit 등)
        ============================================ */
        if (requestURI.contains("/user/mypage") || 
            requestURI.contains("/user/edit") || 
            requestURI.contains("/user/update")) {
            // 로그인 안 됨 → 로그인 페이지로 리다이렉트
            if (user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
        }
        
        /* ============================================
           리뷰 작성 (/review/write, /review/reply)
        ============================================ */
        if (requestURI.contains("/review/write") || 
            requestURI.contains("/review/reply")) {
            // 로그인 안 됨 → 로그인 페이지로 리다이렉트
            if (user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
        }
        
        /* ============================================
           게시판 작성/수정/삭제 (/board/create, /board/edit, /board/delete)
        ============================================ */
        if (requestURI.contains("/board/create") || 
            requestURI.contains("/board/edit") || 
            requestURI.contains("/board/delete") ||
            requestURI.contains("/board/save")) {
            // 로그인 안 됨 → 로그인 페이지로 리다이렉트
            if (user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
        }
        
        /* ============================================
           식당 수정 (/restaurant/edit/**)
        ============================================ */
        if (requestURI.contains("/restaurant/edit")) {
            // 로그인 안 됨 → 로그인 페이지로 리다이렉트
            if (user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
            
            // ADMIN/OWNER만 가능 → 권한 없으면 예외 던지기
            if (!"ROLE_ADMIN".equals(user.getRole()) && !"ROLE_OWNER".equals(user.getRole())) {
                throw new AccessDeniedException("식당 수정 권한이 없습니다");
            }
        }
        
        /* ============================================
           식당 추가 (/restaurant/add)
        ============================================ */
        if (requestURI.contains("/restaurant/add")) {
            // ADMIN만 가능
            if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
                throw new AccessDeniedException("관리자 권한이 필요합니다");
            }
        }
        
        /* ============================================
           식당 삭제 (/restaurant/delete/**)
        ============================================ */
        if (requestURI.contains("/restaurant/delete")) {
            // ADMIN만 가능
            if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
                throw new AccessDeniedException("관리자 권한이 필요합니다");
            }
        }
        
        // 다음 핸들러로 진행
        return true;
    }
}