package org.embed.controller;

import org.embed.dto.UserDTO;
import org.embed.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final UserService userService;
    
    /* ============================================
       카카오 설정
    ============================================ */
    @Value("${kakao.client.id}")
    private String kakaoClientId;
    
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
    
    /* ============================================
       네이버 설정
    ============================================ */
    @Value("${naver.client.id}")
    private String naverClientId;
    
    @Value("${naver.client.secret}")
    private String naverClientSecret;
    
    @Value("${naver.redirect.uri}")
    private String naverRedirectUri;

    /* ============================================
       카카오 로그인
    ============================================ */
    
    @GetMapping("/kakao")
    public String kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        System.out.println("===== 카카오 콜백 시작 =====");
        System.out.println("code: " + code);
        
        try {
            String accessToken = getKakaoAccessToken(code);
            System.out.println("accessToken 받음: " + accessToken);
            
            Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
            System.out.println("userInfo: " + userInfo);
            
            Long kakaoId = ((Number) userInfo.get("id")).longValue();
            System.out.println("kakaoId: " + kakaoId);
            
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (String) profile.get("nickname");
            
            String email = "kakao_" + kakaoId + "@kakao.user";
            System.out.println("생성된 이메일: " + email);
            
            UserDTO user = userService.findByEmail(email);
            
            // 신규 회원
            if (user == null) {
                System.out.println(">>> 신규 회원 - 최소 정보만 저장");
                user = new UserDTO();
                user.setEmail(email);
                user.setName(nickname);
                user.setProvider("kakao");
                user.setRole("ROLE_USER");
                user.setIsDeleted("N");
                
                // 최소 정보만 DB에 저장
                userService.insertUser(user);
                
                // DB에서 다시 조회 (ID 포함)
                user = userService.findByEmail(email);
                
                // 세션에 임시 저장
                session.setAttribute("tempUser", user);
                System.out.println(">>> 추가 정보 입력 페이지로 이동");
                
                return "redirect:/user/sns-additional-info";
            }
            
            // 기존 회원
            System.out.println(">>> 기존 회원 로그인");
            authenticateAndSaveSession(user, email, session);
            
            return "redirect:/";
            
        } catch (Exception e) {
            System.out.println("카카오 로그인 에러!");
            e.printStackTrace();
            return "redirect:/user/login?error=true";
        }
    }

    private String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        
        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, request, Map.class
        );
        
        return response.getBody();
    }

    /* ============================================
       네이버 로그인
    ============================================ */
    
    @GetMapping("/naver")
    public String naverLogin() {
        String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize"
                + "?client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                + "&response_type=code"
                + "&state=STATE_STRING";
        return "redirect:" + naverAuthUrl;
    }

    @GetMapping("/naver/callback")
    public String naverCallback(@RequestParam("code") String code, 
                                @RequestParam("state") String state, 
                                HttpSession session) {
        System.out.println("===== 네이버 콜백 시작 =====");
        System.out.println("code: " + code);
        System.out.println("state: " + state);
        
        try {
            String accessToken = getNaverAccessToken(code, state);
            System.out.println("accessToken 받음: " + accessToken);
            
            Map<String, Object> userInfo = getNaverUserInfo(accessToken);
            System.out.println("userInfo: " + userInfo);
            
            Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
            String naverId = (String) response.get("id");
            String nickname = (String) response.get("name");
            String email = (String) response.get("email");
            
            System.out.println("naverId: " + naverId);
            System.out.println("nickname: " + nickname);
            System.out.println("email: " + email);
            
            if (email == null || email.isEmpty()) {
                email = "naver_" + naverId + "@naver.user";
                System.out.println("생성된 이메일: " + email);
            }
            
            UserDTO user = userService.findByEmail(email);
            
            // 신규 회원
            if (user == null) {
                System.out.println(">>> 신규 회원 - 최소 정보만 저장");
                user = new UserDTO();
                user.setEmail(email);
                user.setName(nickname);
                user.setProvider("naver");
                user.setRole("ROLE_USER");
                user.setIsDeleted("N");
                
                // 최소 정보만 DB에 저장
                userService.insertUser(user);
                
                // DB에서 다시 조회 (ID 포함)
                user = userService.findByEmail(email);
                
                // 세션에 임시 저장
                session.setAttribute("tempUser", user);
                System.out.println(">>> 추가 정보 입력 페이지로 이동");
                
                return "redirect:/user/sns-additional-info";
            }
            
            // 기존 회원
            System.out.println(">>> 기존 회원 로그인");
            authenticateAndSaveSession(user, email, session);
            
            return "redirect:/";
            
        } catch (Exception e) {
            System.out.println("네이버 로그인 에러!");
            e.printStackTrace();
            return "redirect:/user/login?error=true";
        }
    }

    private String getNaverAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", state);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        
        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getNaverUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, request, Map.class
        );
        
        return response.getBody();
    }

    /* ============================================
       공통: Spring Security와 Session 연결
    ============================================ */
    
    private void authenticateAndSaveSession(UserDTO user, String email, HttpSession session) {
        // 권한(Role) 객체 생성
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        System.out.println("권한 설정: " + user.getRole());
        
        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email,
            null,
            authorities
        );
        System.out.println("Authentication 객체 생성");
        
        // SecurityContext 생성 및 저장
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        System.out.println("SecurityContext에 인증 정보 저장");
        
        // 세션에 저장 (Thymeleaf 템플릿에서 사용)
        session.setAttribute("user", user);
        System.out.println("세션에 사용자 정보 저장");
        
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        System.out.println("SecurityContext를 세션에 저장");
    }
}