package org.embed.controller;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.dto.RestaurantDTO;
import org.embed.dto.UserDTO;
import org.embed.service.BoardService;
import org.embed.service.RestaurantService;
import org.embed.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;
    private final RestaurantService restaurantService;

    /* ============================================
       관리자 대시보드
    ============================================ */
    
    // 관리자 대시보드 (관리자만 접근 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            @RequestParam(name = "userPage", defaultValue = "1") int userPage,
            @RequestParam(name = "restaurantPage", defaultValue = "1") int restaurantPage,
            @RequestParam(name = "boardPage", defaultValue = "1") int boardPage,
            @RequestParam(name = "noticePage", defaultValue = "1") int noticePage,
            @RequestParam(name = "userKeyword", required = false) String userKeyword,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "restaurantKeyword", required = false) String restaurantKeyword,
            @RequestParam(name = "ownerFilter", required = false) String ownerFilter,
            @RequestParam(name = "filter", required = false) String filter,
            Model model) {

        int limit = 10;

        // 사용자 관리 데이터
        userKeyword = normalizeParam(userKeyword);
        status = normalizeParam(status);
        role = normalizeParam(role);

        int userOffset = (userPage - 1) * limit;
        List<UserDTO> users = userService.searchUsers(userKeyword, status, role, userOffset, limit);
        int userTotal = userService.countSearchUsers(userKeyword, status, role);

        model.addAttribute("users", users);
        model.addAttribute("userTotalPages", calculateTotalPages(userTotal, limit));
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", userKeyword);
        model.addAttribute("status", status);
        model.addAttribute("role", role);

        // 식당 관리 데이터
        int restaurantOffset = (restaurantPage - 1) * limit;
        List<RestaurantDTO> restaurants = restaurantService.findAll(restaurantKeyword, ownerFilter, restaurantOffset, limit);
        int restaurantTotal = restaurantService.countAllWithOwner(restaurantKeyword, ownerFilter);

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("restaurantTotalPages", calculateTotalPages(restaurantTotal, limit));
        model.addAttribute("restaurantKeyword", restaurantKeyword);
        model.addAttribute("ownerFilter", ownerFilter);
        model.addAttribute("owners", userService.findByRole("ROLE_OWNER"));
        model.addAttribute("assignedOwnerIds", restaurantService.findAssignedOwnerIds());

        // 미답변 요청 데이터
        int boardOffset = (boardPage - 1) * limit;
        List<BoardDTO> boards = boardService.findUnansweredRequests(boardOffset, limit, filter);
        int boardTotal = boardService.countUnansweredRequests(filter);

        model.addAttribute("boards", boards);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("boardTotalPages", calculateTotalPages(boardTotal, limit));

        // 공지사항 데이터
        int noticeOffset = (noticePage - 1) * limit;
        List<BoardDTO> notices = boardService.findAllNotices(noticeOffset, limit);
        int noticeTotal = boardService.countAllNotices();

        model.addAttribute("notices", notices);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("noticeTotalPages", calculateTotalPages(noticeTotal, limit));

        return "admin/admin-dashboard";
    }

    // 미답변 요청 조회 (관리자만 접근 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/requests")
    public String getAdminRequests(
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        int limit = 10;
        int offset = (page - 1) * limit;

        List<BoardDTO> requests = boardService.findUnansweredRequests(offset, limit, filter);
        int totalCount = boardService.countUnansweredRequests(filter);

        model.addAttribute("requests", requests);
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", calculateTotalPages(totalCount, limit));
        model.addAttribute("totalCount", totalCount);

        return "admin/admin-requests";
    }

    /* ============================================
       권한 관리
    ============================================ */
    
    // 사용자 역할 변경 (관리자만 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/update-role")
    public String updateUserRole(@RequestParam(name = "id") Long userId,
                                 @RequestParam(name = "role") String role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/dashboard?tab=user";
    }

    // 식당 오너 지정 (관리자만 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/update-owner")
    public String updateRestaurantOwner(@RequestParam(name = "restaurantId") Long restaurantId,
                                        @RequestParam(name = "ownerId", required = false) Long ownerId) {
        restaurantService.updateOwner(restaurantId, ownerId);
        return "redirect:/admin/dashboard?tab=restaurant";
    }

    /* ============================================
       게시판 관리
    ============================================ */

    // 관리자 답글 제출 (관리자만 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reply")
    public String submitAdminReply(
            @ModelAttribute BoardDTO reply,
            RedirectAttributes redirectAttributes) {

        try {
            // SecurityContextHolder에서 현재 사용자 인증 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/admin/dashboard?tab=board";
            }

            String email = authentication.getName();
            UserDTO currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "현재 사용자 정보를 찾을 수 없습니다.");
                return "redirect:/admin/dashboard?tab=board";
            }

            // 부모글(원글) 정보 가져오기
            BoardDTO parentPost = boardService.findById(reply.getParentId());
            if (parentPost == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "해당 게시글을 찾을 수 없습니다.");
                return "redirect:/admin/dashboard?tab=board";
            }

            // 카테고리 검증 (QUESTION 또는 SUGGESTION만 가능)
            if (!"QUESTION".equals(parentPost.getCategory()) && !"SUGGESTION".equals(parentPost.getCategory())) {
                redirectAttributes.addFlashAttribute("errorMessage", "답글을 작성할 수 없는 게시글입니다.");
                return "redirect:/admin/requests";
            }

            // 답글 정보 설정
            reply.setCategory(parentPost.getCategory());
            reply.setUserId(currentUser.getId());

            // 답글 저장
            int result = boardService.insertAdminReply(reply);

            if (result > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "답글이 작성되었습니다.");
                return "redirect:/admin/dashboard?tab=board";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "답글 저장에 실패했습니다. DB 오류를 확인하세요.");
                return "redirect:/admin/dashboard?tab=board";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "답글 작성 중 오류가 발생했습니다.");
            return "redirect:/admin/dashboard?tab=board";
        }
    }

    /* ============================================
       헬퍼 메서드
    ============================================ */

    // 현재 로그인한 사용자 정보 가져오기
    private UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    // 파라미터 정규화
    private String normalizeParam(String param) {
        if (param == null || "null".equalsIgnoreCase(param) || param.isBlank()) {
            return null;
        }
        return param;
    }

    // 이 페이지 수 계산
    private int calculateTotalPages(int total, int limit) {
        if (total == 0) return 0;
        return (int) Math.ceil((double) total / limit);
    }
}