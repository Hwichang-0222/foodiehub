package org.embed.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class IndexController implements ErrorController {
    
    @GetMapping("/")
    public String getIndex() {
        return "index";
    }
    
    /* ============================================
       에러 페이지 처리 (Spring Boot 기본 에러 오버라이드)
       실제 에러 내용은 GlobalExceptionHandler 또는 
       SecureConfiguration에서 request에 담아서 전달
    ============================================ */
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            // 어떤 상태 코드든 같은 에러 페이지
            if (statusCode == HttpStatus.FORBIDDEN.value() ||
                statusCode == HttpStatus.NOT_FOUND.value() ||
                statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/error";
            }
        }
        return "error/error";
    }
}