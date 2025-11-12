package org.embed.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecureConfiguration {

    /* ============================================
       비밀번호 암호화
    ============================================ */

    // BCrypt 비밀번호 인코더 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ============================================
       Security FilterChain 설정
    ============================================ */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화
            .csrf(csrf -> csrf.disable())
            
            // 요청에 대한 권한 설정
            .authorizeHttpRequests(auth -> auth
                /* ============================================
                   공개 페이지 (모두 접근 가능)
                ============================================ */
                .requestMatchers("/", "/css/**", "/js/**", "/images/**","/debug/auth").permitAll()
                .requestMatchers("/error", "/error/**").permitAll()
                
                /* ============================================
                   사용자 인증 (회원가입, 로그인)
                ============================================ */
                .requestMatchers("/oauth/**", "/user/signup", "/user/login", "/user/logout", "/user/check-email", 
                                "/user/find-id", "/user/find-password").permitAll()
                
                /* ============================================
                   맛집 (Restaurant) - P0, P2
                ============================================ */
                // 맛집 목록 및 상세 - 공개
                .requestMatchers("/restaurant/list", "/restaurant/detail/**").permitAll()
                
                // 맛집 추가 (P0: 1, 2번 - ADMIN만)
                .requestMatchers("/restaurant/add").hasAuthority("ROLE_ADMIN")
                
                // 맛집 수정 (P2: 7번 - 로그인 필수)
                // ADMIN: 모든 맛집 수정 가능
                // OWNER: 본인 맛집만 수정 가능 (owner_id == user.id 비교)
                .requestMatchers("/restaurant/edit/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                
                // 맛집 삭제 (P0: 3번 - ADMIN만)
                .requestMatchers("/restaurant/delete/**").hasAuthority("ROLE_ADMIN")
                
                /* ============================================
                   리뷰 (Review) - P1
                ============================================ */
                // 리뷰 작성 (P1: 4번), 리뷰 답글 (P1: 5번) - 로그인 필수
                .requestMatchers("/review/write", "/review/reply").authenticated()
                
                /* ============================================
                   게시판 (Board) - P1, P2
                   
                   4개 탭: NOTICE(공지), GENERAL(일반), QUESTION(문의), SUGGESTION(건의)
                ============================================ */
                // 게시판 목록, 검색 - 공개 (4개 탭 모두 조회 가능)
                .requestMatchers("/board/list", "/board/search").permitAll()
                
                // 게시글 상세 (P1: 6번)
                // NOTICE(공지): 공개 (누구나 조회)
                // GENERAL(일반): 로그인 필수 (누구나 조회 가능)
                // QUESTION(문의): 로그인 필수 (작성자/ADMIN만 조회 가능)
                // SUGGESTION(건의): 로그인 필수 (누구나 조회 가능)
                // (컨트롤러에서 세부 권한 체크)
                .requestMatchers("/board/detail/**").permitAll()
                
                // 게시글 작성 폼, 작성 - 로그인 필수
                .requestMatchers("/board/create", "/board/save").authenticated()
                
                // 게시글 수정 (P2: 8번 - 로그인 필수)
                // (컨트롤러에서 작성자만 가능 - ADMIN 제외)
                .requestMatchers("/board/edit/**").authenticated()
                
                // 게시글 삭제 (P2: 9번 - 로그인 필수)
                // (컨트롤러에서 작성자만 가능 - ADMIN 제외)
                .requestMatchers("/board/delete/**").authenticated()
                
                /* ============================================
                   관리자 (Admin) - P3
                ============================================ */
                // 관리자 페이지 전체 - ADMIN만
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                
                /* ============================================
                   기본값 - 로그인 필수
                ============================================ */
                .anyRequest().authenticated()
            )
            
            // 로그인 설정 (폼 기반 로그인 비활성화, 커스텀 컨트롤러 사용)
            .formLogin(login -> login.disable())
            
            // 로그아웃 설정 (커스텀 컨트롤러 사용)
            .logout(logout -> logout.disable());
                        
        return http.build();
    }
}