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

    /* ------------------------------
       회원가입
    ------------------------------ */
    // 회원가입 폼 페이지
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO user,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                         Model model) {

        // 이메일 중복 확인
        if (userService.countByEmail(user.getEmail()) > 0) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/signup";
        }

        user.setRole("ROLE_USER");
        user.setIsDeleted("N");

        // 프로필 이미지 처리
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

    /* ------------------------------
       로그인/로그아웃
    ------------------------------ */
    // 로그인 폼 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        boolean isValid = userService.validateLogin(email, password);

        if (isValid) {
            UserDTO user = userService.findByEmail(email);
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /* ------------------------------
       마이페이지
    ------------------------------ */
    // 마이페이지 (내가 쓴 리뷰, 게시글 목록)
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

        return "user/mypage";
    }

    /* ------------------------------
       회원정보 수정
    ------------------------------ */
    // 회원정보 수정 폼 페이지
    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);
        return "user/edit";
    }

    // 회원정보 수정 처리
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO user,
                             @RequestParam("currentPassword") String currentPassword,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(value = "profileImage", required = false) MultipartFile file,
                             HttpSession session,
                             Model model) {

        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO sessionUser = (UserDTO) userObj;

        // 현재 비밀번호 검증
        boolean isValid = userService.validateLogin(sessionUser.getEmail(), currentPassword);
        if (!isValid) {
            model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
            model.addAttribute("user", sessionUser);
            return "user/edit";
        }

        // 새 비밀번호 입력 시 변경, 아니면 기존 유지
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        } else {
            user.setPassword(sessionUser.getPassword());
        }

        // 프로필 이미지 업로드
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
                return "user/edit";
            }
        } else {
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

        model.addAttribute("success", "회원정보가 수정되었습니다.");
        return "redirect:/user/mypage";
    }

    /* ------------------------------
       회원 탈퇴
    ------------------------------ */
    // 회원 탈퇴 처리
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;

        // 논리 삭제 (is_deleted = 'Y')
        userService.softDeleteUser(user.getId());

        // 세션 종료
        session.invalidate();

        return "redirect:/";
    }

    /* ------------------------------
       유틸리티 메서드
    ------------------------------ */
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
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
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