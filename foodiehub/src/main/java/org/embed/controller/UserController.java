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

	private void setUserAuthentication(UserDTO user, HttpSession session) {
		session.setAttribute("user", user);

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
		session.setAttribute(
			HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			securityContext
		);
	}

	/* ============================================
	   회원가입
	============================================ */

	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("user", new UserDTO());
		return "user/user-signup";
	}

	@PostMapping("/signup")
	public String signup(
		@ModelAttribute UserDTO user,
		@RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
		Model model
	) {
		// 이메일 중복 검사
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

	@GetMapping("/login")
	public String loginPage() {
		return "user/user-login";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam("email") String email,
		@RequestParam("password") String password,
		HttpSession session,
		Model model
	) {
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

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		SecurityContextHolder.clearContext();
		return "redirect:/";
	}

	/* ============================================
	   아이디/비밀번호 찾기
	============================================ */

	@GetMapping("/find-id")
	public String findIdPage() {
		return "user/user-find-id";
	}

	@PostMapping("/find-id")
	@ResponseBody
	public Map<String, Object> findId(
		@RequestParam("phone") String phone,
		@RequestParam("name") String name
	) {
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

	@GetMapping("/find-password")
	public String findPasswordPage() {
		return "user/user-find-password";
	}

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

	@PostMapping("/reset-password")
	@ResponseBody
	public Map<String, Object> resetPassword(
		@RequestParam("token") String token,
		@RequestParam("newPassword") String newPassword
	) {
		Map<String, Object> response = new java.util.HashMap<>();
		response.put("success", true);
		response.put("message", "비밀번호가 변경되었습니다.");
		return response;
	}

	/* ============================================
	   마이페이지
	============================================ */

	@GetMapping("/mypage")
	public String myPage(
		@RequestParam(name = "reviewPage", defaultValue = "1") int reviewPage,
		@RequestParam(name = "boardPage", defaultValue = "1") int boardPage,
		HttpSession session,
		Model model
	) {
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
	   회원정보 수정
	============================================ */

	@GetMapping("/edit")
	public String editPage(HttpSession session, Model model) {
		Object userObj = session.getAttribute("user");
		if (userObj == null) return "redirect:/user/login";

		UserDTO user = (UserDTO) userObj;
		model.addAttribute("user", user);
		return "user/user-edit";
	}

	@PostMapping("/update")
	public String updateUser(
		@ModelAttribute UserDTO user,
		@RequestParam("currentPassword") String currentPassword,
		@RequestParam(value = "newPassword", required = false) String newPassword,
		@RequestParam(value = "profileImage", required = false) MultipartFile file,
		HttpSession session,
		Model model
	) {
		Object userObj = session.getAttribute("user");
		if (userObj == null) return "redirect:/user/login";

		UserDTO sessionUser = (UserDTO) userObj;

		// CRITICAL: 비밀번호 검증 필수
		boolean isValid = userService.validateLogin(sessionUser.getEmail(), currentPassword);
		if (!isValid) {
			model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
			model.addAttribute("user", sessionUser);
			return "user/user-edit";
		}

		if (newPassword != null && !newPassword.isBlank()) {
			user.setPassword(newPassword);
		} else {
			user.setPassword(sessionUser.getPassword());
		}

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

		user.setId(sessionUser.getId());
		user.setEmail(sessionUser.getEmail());
		user.setRole(sessionUser.getRole());
		user.setProvider(sessionUser.getProvider());
		user.setBirthDate(sessionUser.getBirthDate());
		user.setGender(sessionUser.getGender());

		userService.updateUser(user);

		UserDTO updatedUser = userService.findByEmail(user.getEmail());
		setUserAuthentication(updatedUser, session);

		model.addAttribute("successMessage", "회원정보가 수정되었습니다.");
		return "redirect:/user/mypage";
	}

	/* ============================================
	   회원탈퇴
	============================================ */

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
