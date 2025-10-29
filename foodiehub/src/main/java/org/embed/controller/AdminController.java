package org.embed.controller;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.dto.RestaurantDTO;
import org.embed.dto.UserDTO;
import org.embed.service.BoardService;
import org.embed.service.RestaurantService;
import org.embed.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;
    private final RestaurantService restaurantService;

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
            HttpSession session,
            Model model) {
    	

        UserDTO loginUser = (UserDTO) session.getAttribute("user");
        if (loginUser == null || !loginUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/";
        }

        int limit = 10;

        /* ------------------------------ 
        1. 유저 목록 (검색 + 상태/역할 필터 + 페이지네이션)
	     ------------------------------ */
	     if ("null".equalsIgnoreCase(userKeyword)) userKeyword = null;
	     if ("null".equalsIgnoreCase(status)) status = null;
	     if ("null".equalsIgnoreCase(role)) role = null;

	     int offset = (userPage - 1) * limit;
	
	     List<UserDTO> users;
	     int userTotal;
	
	     // 검색 또는 필터 조건이 없을 때
	     if ((userKeyword == null || userKeyword.isBlank()) && 
	         (status == null || status.isBlank()) && 
	         (role == null || role.isBlank())) {
	         users = userService.findAllUsers(userPage, limit);
	         userTotal = userService.countAllUsers();
	     } else {
	         users = userService.searchUsers(userKeyword, status, role, offset, limit);
	         userTotal = userService.countSearchUsers(userKeyword, status, role);
	     }
	
	     int userTotalPages = (userTotal > 0)
	             ? (int) Math.ceil((double) userTotal / limit)
	             : 0;
	
	     model.addAttribute("users", users);
	     model.addAttribute("userTotalPages", userTotalPages);
	     model.addAttribute("userPage", userPage);
	     model.addAttribute("keyword", userKeyword);
	     model.addAttribute("status", status);
	     model.addAttribute("role", role);

        /* ------------------------------
           2. 게시판 / 공지사항
        ------------------------------ */
        // 답글 관리 (미답변 요청글만)
        List<BoardDTO> boards = boardService.findUnansweredRequests(boardPage, limit, filter);
        int boardTotal = boardService.countUnansweredRequests(filter);
        int boardTotalPages = (int) Math.ceil((double) boardTotal / limit);
        if (boardTotalPages == 0) boardTotalPages = 1;

        // 공지사항 (BoardService 내 NOTICE 카테고리)
        List<BoardDTO> notices = boardService.findAllNotices(noticePage, limit);
        int noticeTotal = boardService.countAllNotices();
        int noticeTotalPages = (int) Math.ceil((double) noticeTotal / limit);
        if (noticeTotalPages == 0) noticeTotalPages = 1;

        /* ------------------------------
           3. 식당 목록 (검색 + 필터 + 페이지네이션)
        ------------------------------ */
        int resoffset = (restaurantPage - 1) * limit;
        int restaurantTotal = restaurantService.countAllWithOwner(restaurantKeyword, ownerFilter);
        int restaurantTotalPages = (int) Math.ceil((double) restaurantTotal / limit);
        if (restaurantTotalPages == 0) restaurantTotalPages = 1;

        List<RestaurantDTO> restaurants = restaurantService.findAll(restaurantKeyword, ownerFilter, resoffset, limit);
        List<UserDTO> owners = userService.findByRole("ROLE_OWNER");
        List<Long> assignedOwnerIds = restaurantService.findAssignedOwnerIds();

        /* ------------------------------
           4. 모델 전달
        ------------------------------ */

        // 게시판 / 공지
        model.addAttribute("boards", boards);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("boardTotalPages", boardTotalPages);

        model.addAttribute("notices", notices);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("noticeTotalPages", noticeTotalPages);

        // 식당
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("owners", owners);
        model.addAttribute("assignedOwnerIds", assignedOwnerIds);
        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("restaurantTotalPages", restaurantTotalPages);

        // 검색/필터 값
        model.addAttribute("restaurantKeyword", restaurantKeyword);
        model.addAttribute("ownerFilter", ownerFilter);

        return "admin/admin-dashboard";
    }

    /* ------------------------------
       5. 유저 역할 변경
    ------------------------------ */
    @GetMapping("/admin/update-role")
    public String updateUserRole(@RequestParam(name = "id") Long userId,
                                 @RequestParam(name = "role") String role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/dashboard?tab=user";
    }

    /* ------------------------------
       6. 식당 오너 지정
    ------------------------------ */
    @GetMapping("/admin/update-owner")
    public String updateRestaurantOwner(@RequestParam(name = "restaurantId") Long restaurantId,
                                        @RequestParam(name = "ownerId", required = false) Long ownerId) {
        restaurantService.updateOwner(restaurantId, ownerId);
        return "redirect:/admin/dashboard?tab=restaurant";
    }
}
