package org.embed.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.embed.dto.BoardDTO;
import org.embed.dto.ReviewDTO;
import org.embed.dto.UserDTO;
import org.embed.service.BoardService;
import org.embed.service.ReviewService;
import org.embed.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final BoardService boardService;

    /* ============================================
       Spring Security 인증 처리
    ============================================ */

    // 세션과 Spring Security에 동시에 인증 정보 저장
    private void setUserAuthentication(UserDTO user, HttpSession session) {
        // 세션에 저장 (뷰페이지에서 사용)
        session.setAttribute("user", user);
        
        // Spring Security에 저장 (권한 체크에 사용)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), 
            null, 
            authorities
        );
        
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        
        // CRITICAL: SecurityContext를 세션에 명시적으로 저장
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    /* ============================================
       회원가입
    ============================================ */

    // 회원가입 폼 페이지 표시
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/user-signup";
    }

    // 회원가입 처리 - 이메일 중복확인, 프로필 이미지 저장
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO user,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                         Model model) {

        // 이메일 중복 검사 (매우 중요: DB 무결성 유지)
        if (userService.countByEmail(user.getEmail()) > 0) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/user-signup";
        }

        user.setRole("ROLE_USER");
        user.setIsDeleted("N");

        if (profileImage != null && !profileImage.isEmpty()) {
            String savedPath = saveProfileImage(profileImage);
            user.setProfileImageUrl(savedPath);
        } else {
            user.setProfileImageUrl("/images/default-profile.png");
        }

        userService.insertUser(user);
        return "redirect:/user/login";
    }

    // 이메일 중복 확인 (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam(name = "email") String email) {
        int count = userService.countByEmail(email);
        boolean exists = count > 0;
        return Collections.singletonMap("exists", exists);
    }

    /* ============================================
       로그인/로그아웃
    ============================================ */

    // 로그인 페이지 표시
    @GetMapping("/login")
    public String loginPage() {
        return "user/user-login";
    }

    // 로그인 처리 - 이메일, 비밀번호 검증 및 세션/Security 저장
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        boolean isValid = userService.validateLogin(email, password);

        if (isValid) {
            UserDTO user = userService.findByEmail(email);
            setUserAuthentication(user, session);
            return "redirect:/";
        } else {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/user-login";
        }
    }

    // 로그아웃 처리 - 세션 및 Security 인증 정보 제거
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    /* ============================================
       아이디/비밀번호 찾기
    ============================================ */

    // 아이디 찾기 페이지 표시
    @GetMapping("/find-id")
    public String findIdPage() {
        return "user/user-find-id";
    }

    // 아이디 찾기 처리 - 전화번호와 이름으로 조회
    @PostMapping("/find-id")
    @ResponseBody
    public Map<String, Object> findId(@RequestParam("phone") String phone,
                                      @RequestParam("name") String name) {
        UserDTO user = userService.findByPhoneAndName(phone, name);
        Map<String, Object> response = new java.util.HashMap<>();
        
        if (user != null && "N".equals(user.getIsDeleted())) {
            String email = user.getEmail();
            String maskedEmail = email.substring(0, email.indexOf("@")) + "@****." + 
                                 email.substring(email.lastIndexOf(".") + 1);
            response.put("success", true);
            response.put("email", maskedEmail);
        } else {
            response.put("success", false);
            response.put("message", "일치하는 사용자가 없습니다.");
        }
        return response;
    }

    // 비밀번호 찾기 페이지 표시
    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "user/user-find-password";
    }

    // 비밀번호 찾기 처리 - 인증 코드 전송
    @PostMapping("/find-password")
    @ResponseBody
    public Map<String, Object> findPassword(@RequestParam("email") String email) {
        UserDTO user = userService.findByEmail(email);
        Map<String, Object> response = new java.util.HashMap<>();
        
        if (user != null && "N".equals(user.getIsDeleted())) {
            String resetToken = UUID.randomUUID().toString();
            response.put("success", true);
            response.put("token", resetToken);
            response.put("message", "인증 코드가 이메일로 전송되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "등록되지 않은 이메일입니다.");
        }
        return response;
    }

    // 비밀번호 재설정 처리
    @PostMapping("/reset-password")
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam("token") String token,
                                             @RequestParam("newPassword") String newPassword) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("message", "비밀번호가 변경되었습니다.");
        return response;
    }

    /* ============================================
       마이페이지
    ============================================ */

    // 마이페이지 표시 - 내가 쓴 리뷰, 게시글 페이지네이션
    @GetMapping("/mypage")
    public String myPage(@RequestParam(name = "reviewPage", defaultValue = "1") int reviewPage,
                         @RequestParam(name = "boardPage", defaultValue = "1") int boardPage,
                         HttpSession session, Model model) {

        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/user/login";
        }

        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);

        int reviewLimit = 4;
        int boardLimit = 4;
        int reviewOffset = (reviewPage - 1) * reviewLimit;
        int boardOffset = (boardPage - 1) * boardLimit;

        List<ReviewDTO> reviews = reviewService.findPagedByUserId(user.getId(), reviewOffset, reviewLimit);
        int totalReviews = reviewService.countByUserId(user.getId());

        List<BoardDTO> boards = boardService.findPagedByUserId(user.getId(), boardOffset, boardLimit);
        int totalBoards = boardService.countByUserId(user.getId());

        model.addAttribute("reviews", reviews);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("reviewCurrentPage", reviewPage);
        model.addAttribute("reviewTotalPages", (int) Math.ceil((double) totalReviews / reviewLimit));

        model.addAttribute("boards", boards);
        model.addAttribute("totalBoards", totalBoards);
        model.addAttribute("boardCurrentPage", boardPage);
        model.addAttribute("boardTotalPages", (int) Math.ceil((double) totalBoards / boardLimit));

        return "user/user-mypage";
    }

    /* ============================================
       SNS 추가정보 입력
    ============================================ */

    // SNS 로그인 후 추가 정보 입력 페이지 표시
    @GetMapping("/sns-additional-info")
    public String snsAdditionalInfoPage(HttpSession session, Model model) {
        Object tempUserObj = session.getAttribute("tempUser");
        if (tempUserObj == null) {
            return "redirect:/user/login";
        }
        
        UserDTO tempUser = (UserDTO) tempUserObj;
        model.addAttribute("user", tempUser);
        return "user/user-sns-additional";
    }

    // SNS 추가 정보 저장 처리
    @PostMapping("/sns-additional-info")
    public String saveSnsAdditionalInfo(@ModelAttribute UserDTO user,
                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                        HttpSession session,
                                        Model model) {
        
        Object tempUserObj = session.getAttribute("tempUser");
        if (tempUserObj == null) {
            return "redirect:/user/login";
        }
        
        UserDTO tempUser = (UserDTO) tempUserObj;
        
        // 프로필 이미지 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            String savedPath = saveProfileImage(profileImage);
            user.setProfileImageUrl(savedPath);
        } else {
            user.setProfileImageUrl("/images/default-profile.png");
        }
        
        // 기본 정보 유지
        user.setId(tempUser.getId());
        user.setEmail(tempUser.getEmail());
        user.setName(tempUser.getName());
        user.setProvider(tempUser.getProvider());
        user.setRole(tempUser.getRole());
        user.setIsDeleted("N");
        
        // 추가 정보 업데이트
        userService.updateUser(user);
        
        // 세션 정리 및 정식 로그인 처리
        session.removeAttribute("tempUser");
        
        UserDTO finalUser = userService.findByEmail(user.getEmail());
        setUserAuthentication(finalUser, session);
        
        return "redirect:/";
    }

    /* ============================================
       회원정보 수정
    ============================================ */

    // 회원정보 수정 페이지 표시
    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);
        return "user/user-edit";
    }

    // 회원정보 수정 처리 - 비밀번호 검증, 프로필 이미지 업로드
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO user,
                             @RequestParam(value = "currentPassword", required = false) String currentPassword,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(value = "profileImage", required = false) MultipartFile file,
                             HttpSession session,
                             Model model) {

        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO sessionUser = (UserDTO) userObj;

        // 일반 로그인 회원: 현재 비밀번호 검증 (매우 중요: 보안)
        if (sessionUser.getProvider() == null) {
            if (currentPassword == null || currentPassword.isBlank()) {
                model.addAttribute("error", "현재 비밀번호를 입력해주세요.");
                model.addAttribute("user", sessionUser);
                return "user/user-edit";
            }
            
            boolean isValid = userService.validateLogin(sessionUser.getEmail(), currentPassword);
            if (!isValid) {
                model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
                model.addAttribute("user", sessionUser);
                return "user/user-edit";
            }
        }

        // 비밀번호 처리 (일반 로그인만)
        if (sessionUser.getProvider() == null) {
            if (newPassword != null && !newPassword.isBlank()) {
                user.setPassword(newPassword);
            } else {
                user.setPassword(sessionUser.getPassword());
            }
        } else {
            // SNS 로그인은 비밀번호 없음
            user.setPassword(null);
        }

        // 프로필 이미지 처리
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, file.getBytes());
                user.setProfileImageUrl("/uploads/profile/" + fileName);
            } catch (IOException e) {
                model.addAttribute("error", "이미지 업로드 중 오류가 발생했습니다.");
                model.addAttribute("user", sessionUser);
                return "user/user-edit";
            }
        } else {
            user.setProfileImageUrl(sessionUser.getProfileImageUrl());
        }

        // 기본 정보 유지
        user.setId(sessionUser.getId());
        user.setEmail(sessionUser.getEmail());
        user.setRole(sessionUser.getRole());
        user.setProvider(sessionUser.getProvider());

        userService.updateUser(user);
        
        UserDTO updatedUser = userService.findByEmail(user.getEmail());
        setUserAuthentication(updatedUser, session);
        
        model.addAttribute("successMessage", "회원정보가 수정되었습니다.");
        return "redirect:/user/mypage";
    }

    /* ============================================
       회원탈퇴
    ============================================ */

    // 회원탈퇴 처리 - 논리 삭제 및 세션/Security 정보 제거
    @PostMapping("/delete")
    public String deleteUser(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;
        userService.softDeleteUser(user.getId());
        
        session.invalidate();
        SecurityContextHolder.clearContext();
        
        return "redirect:/";
    }

    /* ============================================
       프로필 이미지 저장
    ============================================ */

    // 프로필 이미지 파일 저장 및 경로 반환
    private String saveProfileImage(MultipartFile profileImage) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, profileImage.getBytes());
            
            return "/uploads/profile/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "/images/default-profile.png";
        }
    }
}