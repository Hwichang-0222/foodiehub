package org.embed.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /* ====================================
       회원가입
    ==================================== */
    
    // 1. 회원가입 폼 페이지
    @GetMapping("/signup")
    public String signupForm(Model model) {
        // 빈 사용자 객체 생성해서 뷰에 전달
        model.addAttribute("user", new UserDTO());
        return "user/user-signup";
    }

    // 2. 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO user,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                         Model model) {

        // 이메일 중복 확인
        if (userService.countByEmail(user.getEmail()) > 0) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/user-signup";
        }

        // 기본 역할 및 삭제 여부 설정
        user.setRole("ROLE_USER");
        user.setIsDeleted("N");

        // 프로필 이미지 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            // 프로필 이미지 저장
            String savedPath = saveProfileImage(profileImage);
            user.setProfileImageUrl(savedPath);
        } else {
            // 이미지 없으면 기본 이미지 설정
            user.setProfileImageUrl("/images/default-profile.png");
        }

        // 사용자 정보 DB에 저장
        userService.insertUser(user);
        // 로그인 페이지로 리다이렉트
        return "redirect:/user/login";
    }

    // 3. 이메일 중복 확인 (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam(name = "email") String email) {
        // 이메일 중복 여부 확인
        int count = userService.countByEmail(email);
        // 존재 여부 반환
        boolean exists = count > 0;
        return Collections.singletonMap("exists", exists);
    }

    /* ====================================
       로그인/로그아웃
    ==================================== */
    
    // 4. 로그인 폼 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "user/user-login";
    }

    // 5. 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        // 이메일과 비밀번호로 로그인 검증
        boolean isValid = userService.validateLogin(email, password);

        if (isValid) {
            // 로그인 성공 - 사용자 정보 조회
            UserDTO user = userService.findByEmail(email);
            // 세션에 사용자 정보 저장
            session.setAttribute("user", user);
            // 메인 페이지로 리다이렉트
            return "redirect:/";
        } else {
            // 로그인 실패 - 오류 메시지 표시
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/user-login";
        }
    }

    // 6. 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /* ====================================
       아이디/비밀번호 찾기
    ==================================== */
    
    // 7. 아이디 찾기 페이지
    @GetMapping("/find-id")
    public String findIdPage() {
        return "user/user-find-id";
    }

    // 8. 아이디 찾기 처리
    @PostMapping("/find-id")
    @ResponseBody
    public Map<String, Object> findId(@RequestParam("phone") String phone,
                                      @RequestParam("name") String name) {
        // 전화번호와 이름으로 사용자 조회
        UserDTO user = userService.findByPhoneAndName(phone, name);
        Map<String, Object> response = new java.util.HashMap<>();
        
        if (user != null && "N".equals(user.getIsDeleted())) {
            // 이메일 마스킹 (예: user@****.com)
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

    // 9. 비밀번호 찾기 페이지
    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "user/user-find-password";
    }

    // 10. 비밀번호 찾기 처리 (인증 코드 전송)
    @PostMapping("/find-password")
    @ResponseBody
    public Map<String, Object> findPassword(@RequestParam("email") String email) {
        // 이메일로 사용자 조회
        UserDTO user = userService.findByEmail(email);
        Map<String, Object> response = new java.util.HashMap<>();
        
        if (user != null && "N".equals(user.getIsDeleted())) {
            // 실제로는 이메일로 인증 코드 전송 (여기서는 임시 처리)
            String resetToken = UUID.randomUUID().toString();
            // 토큰을 세션이나 DB에 저장하는 로직 필요
            response.put("success", true);
            response.put("token", resetToken);
            response.put("message", "인증 코드가 이메일로 전송되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "등록되지 않은 이메일입니다.");
        }
        return response;
    }

    // 11. 비밀번호 재설정
    @PostMapping("/reset-password")
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam("token") String token,
                                             @RequestParam("newPassword") String newPassword) {
        // 토큰 검증 및 비밀번호 변경 로직
        Map<String, Object> response = new java.util.HashMap<>();
        // 실제 구현 시 DB에서 토큰 확인 후 비밀번호 변경
        response.put("success", true);
        response.put("message", "비밀번호가 변경되었습니다.");
        return response;
    }

    /* ====================================
       마이페이지
    ==================================== */
    
    // 12. 마이페이지 (내가 쓴 리뷰, 게시글 목록)
    @GetMapping("/mypage")
    public String myPage(@RequestParam(name = "reviewPage", defaultValue = "1") int reviewPage,
                         @RequestParam(name = "boardPage", defaultValue = "1") int boardPage,
                         HttpSession session, Model model) {

        // 세션에서 사용자 정보 조회
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/user/login";
        }

        // 사용자 객체 변환
        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);

        // 페이지당 리뷰와 게시글 개수
        int reviewLimit = 4;
        int boardLimit = 4;

        // 페이지네이션 오프셋 계산
        int reviewOffset = (reviewPage - 1) * reviewLimit;
        int boardOffset = (boardPage - 1) * boardLimit;

        // 리뷰 목록 + 개수
        List<ReviewDTO> reviews = reviewService.findPagedByUserId(user.getId(), reviewOffset, reviewLimit);
        int totalReviews = reviewService.countByUserId(user.getId());

        // 게시글 목록 + 개수
        List<BoardDTO> boards = boardService.findPagedByUserId(user.getId(), boardOffset, boardLimit);
        int totalBoards = boardService.countByUserId(user.getId());

        // 리뷰 관련 model
        model.addAttribute("reviews", reviews);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("reviewCurrentPage", reviewPage);
        model.addAttribute("reviewTotalPages", (int) Math.ceil((double) totalReviews / reviewLimit));

        // 게시글 관련 model
        model.addAttribute("boards", boards);
        model.addAttribute("totalBoards", totalBoards);
        model.addAttribute("boardCurrentPage", boardPage);
        model.addAttribute("boardTotalPages", (int) Math.ceil((double) totalBoards / boardLimit));

        return "user/user-mypage";
    }

    /* ====================================
       회원정보 수정
    ==================================== */
    
    // 13. 회원정보 수정 폼 페이지
    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        // 세션에서 사용자 정보 조회
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        // 사용자 객체 변환
        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);
        return "user/user-edit";
    }

    // 14. 회원정보 수정 처리
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO user,
                             @RequestParam("currentPassword") String currentPassword,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(value = "profileImage", required = false) MultipartFile file,
                             HttpSession session,
                             Model model) {

        // 세션에서 사용자 정보 조회
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        // 사용자 객체 변환
        UserDTO sessionUser = (UserDTO) userObj;

        // 현재 비밀번호 검증
        boolean isValid = userService.validateLogin(sessionUser.getEmail(), currentPassword);
        if (!isValid) {
            // 비밀번호 불일치 - 오류 메시지 표시
            model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
            model.addAttribute("user", sessionUser);
            return "user/user-edit";
        }

        // 새 비밀번호 입력 시 변경, 아니면 기존 유지
        if (newPassword != null && !newPassword.isBlank()) {
            // 새 비밀번호로 변경
            user.setPassword(newPassword);
        } else {
            // 기존 비밀번호 유지
            user.setPassword(sessionUser.getPassword());
        }

        // 프로필 이미지 업로드
        if (file != null && !file.isEmpty()) {
            try {
                // 저장 경로 설정
                String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";
                // 폴더 없으면 자동 생성
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                // 파일명 생성
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                // 파일 저장
                Files.write(filePath, file.getBytes());
                // 프로필 이미지 URL 설정
                user.setProfileImageUrl("/uploads/profile/" + fileName);
            } catch (IOException e) {
                // 이미지 업로드 오류 처리
                model.addAttribute("error", "이미지 업로드 중 오류가 발생했습니다.");
                model.addAttribute("user", sessionUser);
                return "user/user-edit";
            }
        } else {
            // 프로필 이미지 미변경 시 기존 유지
            user.setProfileImageUrl(sessionUser.getProfileImageUrl());
        }

        // 나머지 정보 세팅
        user.setId(sessionUser.getId());
        user.setEmail(sessionUser.getEmail());
        user.setRole(sessionUser.getRole());
        user.setProvider(sessionUser.getProvider());
        user.setBirthDate(sessionUser.getBirthDate());
        user.setGender(sessionUser.getGender());

        // DB 업데이트
        userService.updateUser(user);

        // 세션 갱신
        session.setAttribute("user", userService.findByEmail(user.getEmail()));

        // 수정 완료 후 마이페이지로 리다이렉트
        model.addAttribute("success", "회원정보가 수정되었습니다.");
        return "redirect:/user/mypage";
    }

    /* ====================================
       회원 탈퇴
    ==================================== */
    
    // 15. 회원 탈퇴 처리
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        // 세션에서 사용자 정보 조회
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        // 사용자 객체 변환
        UserDTO user = (UserDTO) userObj;

        // 논리 삭제 (is_deleted = 'Y')
        userService.softDeleteUser(user.getId());

        // 세션 종료
        session.invalidate();

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /* ====================================
       유틸리티 메서드
    ==================================== */
    
    // 프로필 이미지 파일 저장
    private String saveProfileImage(MultipartFile file) {
        try {
            // 저장 경로 설정
            String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";

            // 폴더 없으면 자동 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 파일명 고유화 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                // 확장자 추출
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // UUID로 고유한 파일명 생성
            String newFileName = UUID.randomUUID() + extension;

            // 실제 파일 저장
            File saveFile = new File(uploadDir + newFileName);
            file.transferTo(saveFile);

            // DB에 저장할 상대 경로 반환
            return "/uploads/profile/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            // 저장 실패 시 기본 이미지 반환
            return "/images/default-profile.png";
        }
    }

}