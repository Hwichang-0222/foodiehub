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

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security FilterChain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 공개 페이지
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/data/**", "/debug/auth").permitAll()
                .requestMatchers("/error", "/error/**").permitAll()

                // 사용자 인증 관련
                .requestMatchers("/oauth/**", "/user/signup", "/user/login", "/user/logout",
                                 "/user/check-email", "/user/find-id", "/user/find-password").permitAll()

                // 맛집
                .requestMatchers("/restaurant/list", "/restaurant/detail/**").permitAll()
                .requestMatchers("/restaurant/add").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/restaurant/edit/**", "/menu/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                .requestMatchers("/restaurant/delete/**").hasAuthority("ROLE_ADMIN")
                
                // AI 리뷰 요약 생성 - ADMIN 또는 OWNER
                .requestMatchers("/restaurant/generate-ai-summary/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")

                // 리뷰
                .requestMatchers("/review/write", "/review/reply").authenticated()

                // 게시판
                .requestMatchers("/board/list", "/board/search").permitAll()
                .requestMatchers("/board/detail/**").permitAll()
                .requestMatchers("/board/create", "/board/save",
                                 "/board/edit/**", "/board/delete/**").authenticated()

                // 관리자
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // 기본값
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}
