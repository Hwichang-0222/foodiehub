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

    /* ------------------------------
       관리자 대시보드
    ------------------------------ */
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

        // 관리자 권한 확인
        UserDTO loginUser = (UserDTO) session.getAttribute("user");
        if (loginUser == null || !loginUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/";
        }

        int limit = 10;

        /* ------------------------------
           1. 유저 목록
        ------------------------------ */
        if ("null".equalsIgnoreCase(userKeyword)) userKeyword = null;
        if ("null".equalsIgnoreCase(status)) status = null;
        if ("null".equalsIgnoreCase(role)) role = null;

        int userOffset = (userPage - 1) * limit;

        List<UserDTO> users;
        int userTotal;

        if ((userKeyword == null || userKeyword.isBlank()) &&
            (status == null || status.isBlank()) &&
            (role == null || role.isBlank())) {
            users = userService.searchUsers(null, null, null, userOffset, limit);
            userTotal = userService.countSearchUsers(null, null, null);
        } else {
            users = userService.searchUsers(userKeyword, status, role, userOffset, limit);
            userTotal = userService.countSearchUsers(userKeyword, status, role);
        }

        int userTotalPages = (userTotal > 0) ? (int) Math.ceil((double) userTotal / limit) : 0;

        model.addAttribute("users", users);
        model.addAttribute("userTotalPages", userTotalPages);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", userKeyword);
        model.addAttribute("status", status);
        model.addAttribute("role", role);

        /* ------------------------------
           2. 식당 목록
        ------------------------------ */
        int restaurantOffset = (restaurantPage - 1) * limit;

        List<RestaurantDTO> restaurants = restaurantService.findAll(restaurantKeyword, ownerFilter, restaurantOffset, limit);
        int restaurantTotal = restaurantService.countAllWithOwner(restaurantKeyword, ownerFilter);
        int restaurantTotalPages = (int) Math.ceil((double) restaurantTotal / limit);
        if (restaurantTotalPages == 0) restaurantTotalPages = 1;

        List<UserDTO> owners = userService.findByRole("ROLE_OWNER");
        List<Long> assignedOwnerIds = restaurantService.findAssignedOwnerIds();

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("restaurantTotalPages", restaurantTotalPages);
        model.addAttribute("restaurantKeyword", restaurantKeyword);
        model.addAttribute("ownerFilter", ownerFilter);
        model.addAttribute("owners", owners);
        model.addAttribute("assignedOwnerIds", assignedOwnerIds);

        /* ------------------------------
           3. 답글 관리 (미답변 요청글)
        ------------------------------ */
        List<BoardDTO> boards = boardService.findUnansweredRequests(boardPage, limit, filter);
        int boardTotal = boardService.countUnansweredRequests(filter);
        int boardTotalPages = (int) Math.ceil((double) boardTotal / limit);
        if (boardTotalPages == 0) boardTotalPages = 1;

        model.addAttribute("boards", boards);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("boardTotalPages", boardTotalPages);

        /* ------------------------------
           4. 공지사항 관리
        ------------------------------ */
        List<BoardDTO> notices = boardService.findAllNotices(noticePage, limit);
        int noticeTotal = boardService.countAllNotices();
        int noticeTotalPages = (int) Math.ceil((double) noticeTotal / limit);
        if (noticeTotalPages == 0) noticeTotalPages = 1;

        model.addAttribute("notices", notices);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("noticeTotalPages", noticeTotalPages);

        return "admin/admin-dashboard";
    }

    /* ------------------------------
       유저 역할 변경
    ------------------------------ */
    @GetMapping("/admin/update-role")
    public String updateUserRole(@RequestParam(name = "id") Long userId,
                                 @RequestParam(name = "role") String role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/dashboard?tab=user";
    }

    /* ------------------------------
       식당 오너 지정
    ------------------------------ */
    @GetMapping("/admin/update-owner")
    public String updateRestaurantOwner(@RequestParam(name = "restaurantId") Long restaurantId,
                                        @RequestParam(name = "ownerId", required = false) Long ownerId) {
        restaurantService.updateOwner(restaurantId, ownerId);
        return "redirect:/admin/dashboard?tab=restaurant";
    }
}