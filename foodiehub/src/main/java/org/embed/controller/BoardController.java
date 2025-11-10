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

	/* ============================================
	   헬퍼 메서드
	============================================ */

	private UserDTO getSessionUser(HttpSession session) {
		return (UserDTO) session.getAttribute("user");
	}

	private boolean hasEditPermission(UserDTO user, BoardDTO post) {
		if (user == null || post == null) return false;
		return post.getUserId().equals(user.getId());
	}

	private boolean canView(UserDTO user, BoardDTO post) {
		if (user == null || post == null) return false;
		return post.getUserId().equals(user.getId()) || "ROLE_ADMIN".equals(user.getRole());
	}

	/* ============================================
	   게시판 목록 및 검색
	============================================ */

	@GetMapping("/list")
	public String getBoardList(
			@RequestParam(name = "category", defaultValue = "GENERAL") String category,
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {

		int limit = 10;
		int offset = (page - 1) * limit;

		List<BoardDTO> notices = boardService.findNoticesByCategory(category);
		List<BoardDTO> boards = boardService.findNormalPostsByCategory(category, offset, limit);
		int totalCount = boardService.countNormalPostsByCategory(category);
		int totalPages = (int) Math.ceil((double) totalCount / limit);
		int startNumber = totalCount - offset;

		List<BoardDTO> parentPosts = boards.stream()
			.filter(b -> b.getParentId() == null)
			.collect(Collectors.toList());

		UserDTO user = getSessionUser(session);

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

	@GetMapping("/search")
	public String searchBoard(
			@RequestParam(name = "category", defaultValue = "GENERAL") String category,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {

		int limit = 10;
		int offset = (page - 1) * limit;

		List<BoardDTO> boards = boardService.searchBoard(category, keyword, offset, limit);
		List<BoardDTO> notices = boardService.findNoticesByCategory(category);
		int totalCount = boardService.countSearchBoards(category, keyword);
		int totalPages = (int) Math.ceil((double) totalCount / limit);
		int startNumber = totalCount - offset;

		List<BoardDTO> parentPosts = boards.stream()
			.filter(b -> b.getParentId() == null)
			.collect(Collectors.toList());

		UserDTO user = getSessionUser(session);

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

	/* ============================================
	   게시글 상세 조회
	============================================ */

	@GetMapping("/detail/{id}")
	public String getBoardDetail(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			Model model) {

		BoardDTO post = boardService.findById(id);
		UserDTO user = getSessionUser(session);

		if ("GENERAL".equals(post.getCategory())) {
			if (user == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 이용 부탁드립니다.");
		        return "redirect:/board/list?category=" + post.getCategory();
			}
		}

		if ("QUESTION".equals(post.getCategory()) || "SUGGESTION".equals(post.getCategory())) {
		    if (!canView(user, post)) {
		        redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 이용 부탁드립니다.");
		        return "redirect:/board/list?category=" + post.getCategory();
		    }
		}

		if (post.getParentId() != null) {
			BoardDTO parentPost = boardService.findById(post.getParentId());
			if (!canView(user, parentPost)) {
				redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 이용 부탁드립니다.");
				return "redirect:/board/list?category=" + post.getCategory();
			}
			model.addAttribute("parentPost", parentPost);
		}

		boardService.increaseViewCount(id);

		model.addAttribute("post", post);
		model.addAttribute("user", user);

		return "board/board-detail";
	}

	/* ============================================
	   게시글 작성
	============================================ */

	@GetMapping("/create")
	public String showCreateForm(HttpSession session, Model model) {
		model.addAttribute("post", new BoardDTO());
		return "board/board-create";
	}

	/* ============================================
	   게시글 수정
	============================================ */

	@GetMapping("/edit/{id}")
	public String showEditForm(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			Model model) {

		UserDTO user = getSessionUser(session);
		BoardDTO post = boardService.findById(id);

		if (post == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
			return "redirect:/board/list";
		}

		if (!hasEditPermission(user, post)) {
			redirectAttributes.addFlashAttribute("errorMessage", "수정할 권한이 없습니다.");
			return "redirect:/board/detail/" + id;
		}

		model.addAttribute("post", post);
		return "board/board-edit";
	}

	/* ============================================
	   게시글 저장 (작성/수정 통합)
	============================================ */

	@PostMapping("/save")
	public String saveBoard(
			@ModelAttribute BoardDTO post,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		UserDTO user = getSessionUser(session);

		try {
			if (post.getCategory() != null && post.getCategory().startsWith("NOTICE")) {
				if (user == null || !user.getRole().equals("ROLE_ADMIN")) {
					redirectAttributes.addFlashAttribute("errorMessage", "공지사항 작성 권한이 없습니다.");
					return "redirect:/board/create";
				}
			}

			post.setUserId(user.getId());

			if (post.getId() == null || post.getId() == 0) {
				boardService.insertBoard(post);
				redirectAttributes.addFlashAttribute("successMessage", "게시글이 작성되었습니다.");
			} else {
				boardService.updateBoard(post);
				redirectAttributes.addFlashAttribute("successMessage", "게시글이 수정되었습니다.");
				return "redirect:/board/detail/" + post.getId();
			}

			return "redirect:/board/list?category=" + post.getCategory();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다.");
			return "redirect:/board/create";
		}
	}

	/* ============================================
	   게시글 삭제
	============================================ */

	@PostMapping("/delete/{id}")
	public String deleteBoard(
			@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		UserDTO user = getSessionUser(session);
		BoardDTO post = boardService.findById(id);

		if (!hasEditPermission(user, post)) {
			redirectAttributes.addFlashAttribute("errorMessage", "삭제할 권한이 없습니다.");
			return "redirect:/board/detail/" + id;
		}

		try {
			boardService.deleteBoard(id);
			redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
		}

		return "redirect:/board/list?category=" + post.getCategory();
	}

	/* ============================================
	   공지사항 목록 (일반 사용자용)
	============================================ */

	@GetMapping("/notices")
	public String getAllNotices(
			@RequestParam(name = "page", defaultValue = "1") int page,
			HttpSession session,
			Model model) {

		int limit = 10;
		int offset = (page - 1) * limit;

		List<BoardDTO> notices = boardService.findAllNotices(offset, limit);
		int totalCount = boardService.countAllNotices();
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		UserDTO user = getSessionUser(session);

		model.addAttribute("notices", notices);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("user", user);

		return "board/board-notices";
	}
}
