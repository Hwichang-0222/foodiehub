package org.embed.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.embed.dto.BoardDTO;
import org.embed.dto.UserDTO;
import org.embed.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;

	/* ====================================
	   1. 게시판 목록 조회 (카테고리별, 페이지네이션)
	==================================== */

	@GetMapping("/list")
	public String getBoardList(
			@RequestParam(name = "category", defaultValue = "GENERAL") String category,
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {
		
		// 한 페이지에 표시할 게시글 개수
		int limit = 10;
		// 페이지네이션 오프셋 계산
		int offset = (page - 1) * limit;

		// 공지사항 조회
		List<BoardDTO> notices = boardService.findNoticesByCategory(category);
		// 일반 게시글 조회 (카테고리, 오프셋, 리미트)
		List<BoardDTO> boards = boardService.findNormalPostsByCategory(category, offset, limit);

		// 일반 게시글 총 개수 조회
		int totalCount = boardService.countNormalPostsByCategory(category);
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		// 현재 페이지의 시작 번호 계산
		int startNumber = totalCount - offset;

		// 부모글만 필터링
		List<BoardDTO> parentPosts = boards.stream()
			.filter(b -> b.getParentId() == null)
			.collect(Collectors.toList());

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");

		// 모델에 게시판 데이터 담기
		model.addAttribute("boards", boards);
		model.addAttribute("parentPosts", parentPosts);
		model.addAttribute("notices", notices);
		model.addAttribute("category", category);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("startNumber", startNumber);
		model.addAttribute("user", user);

		return "board/board-list";
	}

	/* ====================================
	   2. 게시판 검색 (카테고리 + 키워드)
	==================================== */

	@GetMapping("/search")
	public String searchBoard(
			@RequestParam(name = "category", defaultValue = "GENERAL") String category,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {

		// 한 페이지에 표시할 게시글 개수
		int limit = 10;
		// 페이지네이션 오프셋 계산
		int offset = (page - 1) * limit;

		// 검색 결과 조회
		List<BoardDTO> boards = boardService.searchBoard(category, keyword, offset, limit);
		// 공지사항 조회
		List<BoardDTO> notices = boardService.findNoticesByCategory(category);
		// 검색 결과 총 개수 조회
		int totalCount = boardService.countSearchBoards(category, keyword);
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		// 현재 페이지의 시작 번호 계산
		int startNumber = totalCount - offset;

		// 부모글만 필터링
		List<BoardDTO> parentPosts = boards.stream()
			.filter(b -> b.getParentId() == null)
			.collect(Collectors.toList());

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");

		// 모델에 검색 결과 담기
		model.addAttribute("boards", boards);
		model.addAttribute("parentPosts", parentPosts);
		model.addAttribute("notices", notices);
		model.addAttribute("category", category);
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("startNumber", startNumber);
		model.addAttribute("user", user);

		return "board/board-list";
	}

	/* ====================================
	   3. 게시글 상세 조회 (로그인 필수)
	==================================== */

	@GetMapping("/detail/{id}")
	public String getBoardDetail(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			Model model) {

		// 조회하려는 게시글 정보 조회
		BoardDTO post = boardService.findById(id);

		// 세션에서 현재 로그인한 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");

		// 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트
		if (user == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
			return "redirect:/user/login";
		}

		// QUESTION 카테고리 (등급 요청) - 작성자와 관리자만 조회 가능
		if ("QUESTION".equals(post.getCategory())) {
			// 작성자가 아니고 관리자가 아니면 접근 불가
			if (!post.getUserId().equals(user.getId()) && !"ROLE_ADMIN".equals(user.getRole())) {
				redirectAttributes.addFlashAttribute("errorMessage", "접근 권한이 없습니다.");
				return "redirect:/board/list?category=" + post.getCategory();
			}
		}

		// 댓글인 경우 - 부모글 작성자와 관리자만 조회 가능
		if (post.getParentId() != null) {
			// 부모 게시글 조회
			BoardDTO parentPost = boardService.findById(post.getParentId());
			// 부모글 작성자가 아니고 관리자가 아니면 접근 불가
			if (!parentPost.getUserId().equals(user.getId()) && !"ROLE_ADMIN".equals(user.getRole())) {
				redirectAttributes.addFlashAttribute("errorMessage", "접근 권한이 없습니다.");
				return "redirect:/board/list?category=" + parentPost.getCategory();
			}
			// 모델에 부모 게시글 정보 담기
			model.addAttribute("parentPost", parentPost);
		}

		// 조회수 1 증가 처리
		boardService.increaseViewCount(id);

		// 모델에 게시글과 사용자 정보 담기
		model.addAttribute("post", post);
		model.addAttribute("user", user);

		return "board/board-detail";
	}

	/* ====================================
	   4. 게시글 작성 폼 (로그인 필수)
	==================================== */

	@GetMapping("/create")
	public String showCreateForm(HttpSession session, Model model) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 빈 게시글 객체 생성해서 뷰에 전달
		model.addAttribute("post", new BoardDTO());

		return "board/board-create";
	}

	/* ====================================
	   5. 게시글 수정 폼 (로그인 필수, 작성자/관리자만)
	==================================== */

	@GetMapping("/edit/{id}")
	public String showEditForm(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			Model model) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 기존 게시글 조회
		BoardDTO post = boardService.findById(id);
		// 게시글이 존재하지 않으면 목록으로 이동
		if (post == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
			return "redirect:/board/list";
		}

		// 작성자나 관리자가 아니면 접근 차단
		if (!post.getUserId().equals(user.getId()) && !"ROLE_ADMIN".equals(user.getRole())) {
			redirectAttributes.addFlashAttribute("errorMessage", "수정할 권한이 없습니다.");
			return "redirect:/board/detail/" + id;
		}

		// 모델에 기존 게시글 정보 담기
		model.addAttribute("post", post);

		return "board/board-create";
	}

	/* ====================================
	   6. 게시글 저장 (POST, 작성/수정 통합)
	==================================== */

	@PostMapping("/save")
	public String saveBoard(
			@ModelAttribute BoardDTO post,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		try {
			// 게시글 작성자 ID 설정
			post.setUserId(user.getId());

			// 게시글 작성/수정 구분
			if (post.getId() == null || post.getId() == 0) {
				// 신규 게시글 저장
				boardService.insertBoard(post);
				// 성공 메시지 플래시 어트리뷰트
				redirectAttributes.addFlashAttribute("successMessage", "게시글이 작성되었습니다.");
			} else {
				// 기존 게시글 수정
				boardService.updateBoard(post);
				// 성공 메시지 플래시 어트리뷰트
				redirectAttributes.addFlashAttribute("successMessage", "게시글이 수정되었습니다.");
				// 상세 페이지로 리다이렉트
				return "redirect:/board/detail/" + post.getId();
			}

			// 목록 페이지로 리다이렉트
			return "redirect:/board/list?category=" + post.getCategory();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다.");
			return "redirect:/board/create";
		}
	}

	/* ====================================
	   7. 게시글 삭제
	==================================== */

	@PostMapping("/delete/{id}")
	public String deleteBoard(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 삭제하려는 게시글 정보 조회
		BoardDTO post = boardService.findById(id);

		// 작성자나 관리자가 아니면 접근 차단
		if (!post.getUserId().equals(user.getId()) && !"ROLE_ADMIN".equals(user.getRole())) {
			redirectAttributes.addFlashAttribute("errorMessage", "삭제할 권한이 없습니다.");
			return "redirect:/board/detail/" + id;
		}

		try {
			// 게시글 삭제 처리
			boardService.deleteBoard(id);
			// 성공 메시지 플래시 어트리뷰트
			redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
		} catch (Exception e) {
			// 오류 메시지 플래시 어트리뷰트
			redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
		}

		// 목록 페이지로 리다이렉트
		return "redirect:/board/list?category=" + post.getCategory();
	}

	/* ====================================
	   8. 관리자 기능 - 공지사항 관리
	==================================== */

	@GetMapping("/notices")
	public String getAllNotices(
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {

		// 한 페이지에 표시할 공지사항 개수
		int limit = 10;
		// 페이지네이션 오프셋 계산
		int offset = (page - 1) * limit;

		// 공지사항 조회
		List<BoardDTO> notices = boardService.findAllNotices(offset, limit);
		// 공지사항 총 개수 조회
		int totalCount = boardService.countAllNotices();
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");

		// 모델에 공지사항 담기
		model.addAttribute("notices", notices);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("user", user);

		return "board/board-notices";
	}

	@GetMapping("/notice/create")
	public String showNoticeCreateForm(HttpSession session, Model model) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 관리자 권한 확인
		if (!"ROLE_ADMIN".equals(user.getRole())) {
			return "redirect:/board/list";
		}

		// 빈 공지사항 객체 생성해서 뷰에 전달
		model.addAttribute("notice", new BoardDTO());

		return "board/board-notice-write";
	}

	@PostMapping("/notice/save")
	public String saveNotice(
			@ModelAttribute BoardDTO notice,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 관리자 권한 확인
		if (!"ROLE_ADMIN".equals(user.getRole())) {
			redirectAttributes.addFlashAttribute("errorMessage", "관리자만 공지사항을 작성할 수 있습니다.");
			return "redirect:/board/list";
		}

		try {
			// 공지사항 작성자 ID 설정
			notice.setUserId(user.getId());
			// 일반 카테고리를 NOTICElo 설정
			notice.setCategory("NOTICE");

			// 공지사항 작성/수정 구분
			if (notice.getId() == null || notice.getId() == 0) {
				// 신규 공지사항 저장
				boardService.insertBoard(notice);
				// 성공 메시지 플래시 어트리뷰트
				redirectAttributes.addFlashAttribute("successMessage", "공지사항이 작성되었습니다.");
			} else {
				// 기존 공지사항 수정
				boardService.updateBoard(notice);
				// 성공 메시지 플래시 어트리뷰트
				redirectAttributes.addFlashAttribute("successMessage", "공지사항이 수정되었습니다.");
				// 상세 페이지로 리다이렉트
				return "redirect:/board/detail/" + notice.getId();
			}

			// 공지사항 목록 페이지로 리다이렉트
			return "redirect:/board/notices";
		} catch (Exception e) {
			// 오류 메시지 플래시 어트리뷰트
			redirectAttributes.addFlashAttribute("errorMessage", "공지사항 저장 중 오류가 발생했습니다.");
			return "redirect:/board/notice/create";
		}
	}

	/* ====================================
	   9. 관리자 기능 - 미답변 요청 관리
	==================================== */

	@GetMapping("/admin/requests")
	public String getAdminRequests(
			HttpSession session,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", defaultValue = "1") int page,
			Model model) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		// 관리자 권한 확인
		if (!"ROLE_ADMIN".equals(user.getRole())) {
			return "redirect:/board/list";
		}

		// 한 페이지에 표시할 요청 개수
		int limit = 10;
		// 페이지네이션 오프셋 계산
		int offset = (page - 1) * limit;

		// 미답변 요청 조회
		List<BoardDTO> requests = boardService.findUnansweredRequests(offset, limit, filter);
		// 미답변 요청 총 개수 조회
		int totalCount = boardService.countUnansweredRequests(filter);
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		// 모델에 미답변 요청 담기
		model.addAttribute("requests", requests);
		model.addAttribute("filter", filter);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);

		return "board/board-admin-requests";
	}

	@PostMapping("/admin/reply")
	public String submitAdminReply(
			HttpSession session,
			@ModelAttribute BoardDTO reply,
			RedirectAttributes redirectAttributes) {

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		// 로그인하지 않은 사용자 차단
		if (user == null) {
			return "redirect:/user/login";
		}

		try {
			// 관리자 권한 확인
			if (!"ROLE_ADMIN".equals(user.getRole())) {
				redirectAttributes.addFlashAttribute("errorMessage", "관리자만 답글을 작성할 수 있습니다.");
				return "redirect:/board/admin/requests";
			}

			// 부모 게시글 조회
			BoardDTO parentPost = boardService.findById(reply.getParentId());

			// QUESTION, SUGGESTION 카테고리만 관리자 답글 가능
			if (!"QUESTION".equals(parentPost.getCategory()) && !"SUGGESTION".equals(parentPost.getCategory())) {
				redirectAttributes.addFlashAttribute("errorMessage", "답글을 작성할 수 없는 게시글입니다.");
				return "redirect:/board/admin/requests";
			}

			// 답글 작성자 ID 설정 (관리자)
			reply.setUserId(user.getId());
			// 관리자 답글 저장
			boardService.insertAdminReply(reply);
			// 성공 메시지 플래시 어트리뷰트
			redirectAttributes.addFlashAttribute("successMessage", "답글이 작성되었습니다.");

			// 원글 상세 페이지로 리다이렉트
			return "redirect:/board/detail/" + reply.getParentId();
		} catch (Exception e) {
			// 오류 메시지 플래시 어트리뷰트
			redirectAttributes.addFlashAttribute("errorMessage", "답글 작성 중 오류가 발생했습니다.");
			return "redirect:/board/admin/requests";
		}
	}
}