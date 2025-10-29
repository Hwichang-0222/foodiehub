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

    // 1. 회원가입 폼 이동
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/signup"; // → /templates/user/signup.html
    }

    // 2. 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO user,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                         Model model) {

        if (userService.countByEmail(user.getEmail()) > 0) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/signup";
        }

        user.setRole("ROLE_USER");
        user.setIsDeleted("N");

        if (profileImage != null && !profileImage.isEmpty()) {
            // 실제 이미지 저장 로직 구현 (예: /uploads/)
            String savedPath = saveProfileImage(profileImage);
            user.setProfileImageUrl(savedPath);
        } else {
            // 기본 이미지 경로 세팅
            user.setProfileImageUrl("/images/default-profile.png");
        }

        userService.insertUser(user);
        return "redirect:/login";
    }

    
    // 이메일 중복 확인 (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        int count = userService.countByEmail(email);
        boolean exists = count > 0;
        return Collections.singletonMap("exists", exists);
    }
    
    // 이미지 파일 저장 메서드
    private String saveProfileImage(MultipartFile file) {
        try {
            // 1. 저장 경로 설정 (FoodieHub 기준: src/main/resources/static/uploads)
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

            // 2. 폴더 없으면 자동 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 3. 파일명 고유화 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID() + extension;

            // 4. 실제 파일 저장
            File saveFile = new File(uploadDir + newFileName);
            file.transferTo(saveFile);

            // 5. DB에 저장할 상대 경로 반환 (Thymeleaf에서 접근 가능)
            return "/uploads/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            // 저장 실패 시 기본 이미지 반환
            return "/images/default-profile.png";
        }
    }
    
    // 3. 로그인 
    @GetMapping("/login")
    public String loginPage() {
        return "user/login";  // templates/user/login.html
    }
    
    // 4. 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
    					@RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        // 유효성 검증 (true / false 리턴)
        boolean isValid = userService.validateLogin(email, password);

        if (isValid) {
            // 로그인 성공 시, 유저 정보를 다시 불러와 세션에 저장
            UserDTO user = userService.findByEmail(email);
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }
    }

    // 5. 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
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

        int reviewPageSize = 4;
        int boardPageSize = 4;
        int reviewOffset;
        int boardOffset;
        
        if (reviewPage <= 0) reviewOffset = 0;
        else reviewOffset = (reviewPage - 1) * reviewPageSize;
        
        if (boardPage <= 0) boardOffset = 0;
        else boardOffset = (boardPage - 1) * boardPageSize;

        // 리뷰 목록 + 개수
        List<ReviewDTO> reviews = reviewService.findPagedByUserId(user.getId(), reviewOffset, reviewPageSize);
        int totalReviews = reviewService.countByUserId(user.getId());

        // 게시글 목록 + 개수
        List<BoardDTO> boards = boardService.findPagedByUserId(user.getId(), boardOffset, boardPageSize);
        int totalBoards = boardService.countByUserId(user.getId());

        // 변수명 HTML과 동일하게 설정
        model.addAttribute("reviews", reviews);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("reviewCurrentPage", reviewPage);
        model.addAttribute("reviewTotalPages", (int) Math.ceil((double) totalReviews / reviewPageSize));

        model.addAttribute("boards", boards);
        model.addAttribute("totalBoards", totalBoards);
        model.addAttribute("boardCurrentPage", boardPage);
        model.addAttribute("boardTotalPages", (int) Math.ceil((double) totalBoards / boardPageSize));

        return "user/mypage";
    }

    
    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);
        return "user/edit"; // templates/user/edit.html
    }
    
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

        // 1. 비밀번호 검증
        boolean isValid = userService.validateLogin(sessionUser.getEmail(), currentPassword);
        if (!isValid) {
            model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
            model.addAttribute("user", sessionUser);
            return "user/edit";
        }

        // 2. 새 비밀번호 입력 시 변경, 아니면 기존 유지
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        } else {
            user.setPassword(sessionUser.getPassword());
        }

        // 3. 프로필 이미지 업로드
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/profile/";
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

        // 4. 나머지 정보 세팅
        user.setId(sessionUser.getId());
        user.setEmail(sessionUser.getEmail());
        user.setRole(sessionUser.getRole());
        user.setProvider(sessionUser.getProvider());
        user.setBirthDate(sessionUser.getBirthDate());
        user.setGender(sessionUser.getGender());

        // 5. DB 업데이트
        userService.updateUser(user);

        // 6. 세션 갱신
        session.setAttribute("user", userService.findByEmail(user.getEmail()));

        model.addAttribute("success", "회원정보가 수정되었습니다.");
        return "redirect:/user/mypage";
    }


    // 8. 회원 탈퇴
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return "redirect:/user/login";

        UserDTO user = (UserDTO) userObj;

        // 실제 DB 삭제 or 탈퇴 처리
        userService.softDeleteUser(user.getId());

        // 세션 종료
        session.invalidate();

        return "redirect:/";
    }


}
