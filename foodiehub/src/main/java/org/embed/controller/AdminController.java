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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;
    private final RestaurantService restaurantService;

    /* ====================================
       관리자 대시보드
    ==================================== */
    // 1. 관리자 대시보드 페이지 조회
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

        // 1-1. 관리자 권한 확인
        UserDTO loginUser = (UserDTO) session.getAttribute("user");
        if (loginUser == null || !loginUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/";
        }

        // 1-2. 페이지당 표시할 데이터 개수 설정
        int limit = 10;

        /* ====================================
           2. 사용자 관리
        ==================================== */
        // 2-1. null 값 검증
        if ("null".equalsIgnoreCase(userKeyword)) userKeyword = null;
        if ("null".equalsIgnoreCase(status)) status = null;
        if ("null".equalsIgnoreCase(role)) role = null;

        // 2-2. 사용자 목록 오프셋 계산
        int userOffset = (userPage - 1) * limit;

        // 2-3. 사용자 목록 조회
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

        // 2-4. 사용자 페이지 수 계산
        int userTotalPages = (userTotal > 0) ? (int) Math.ceil((double) userTotal / limit) : 0;

        // 2-5. 모델에 사용자 데이터 추가
        model.addAttribute("users", users);
        model.addAttribute("userTotalPages", userTotalPages);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", userKeyword);
        model.addAttribute("status", status);
        model.addAttribute("role", role);

        /* ====================================
           3. 식당 관리
        ==================================== */
        // 3-1. 식당 목록 오프셋 계산
        int restaurantOffset = (restaurantPage - 1) * limit;

        // 3-2. 식당 목록 조회
        List<RestaurantDTO> restaurants = restaurantService.findAll(restaurantKeyword, ownerFilter, restaurantOffset, limit);
        // 3-3. 식당 이 개수 조회
        int restaurantTotal = restaurantService.countAllWithOwner(restaurantKeyword, ownerFilter);
        // 3-4. 식당 페이지 수 계산
        int restaurantTotalPages = (int) Math.ceil((double) restaurantTotal / limit);
        if (restaurantTotalPages == 0) restaurantTotalPages = 1;

        // 3-5. 오너 목록 조회
        List<UserDTO> owners = userService.findByRole("ROLE_OWNER");
        // 3-6. 이미 지정된 오너 ID 조회
        List<Long> assignedOwnerIds = restaurantService.findAssignedOwnerIds();

        // 3-7. 모델에 식당 데이터 추가
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("restaurantTotalPages", restaurantTotalPages);
        model.addAttribute("restaurantKeyword", restaurantKeyword);
        model.addAttribute("ownerFilter", ownerFilter);
        model.addAttribute("owners", owners);
        model.addAttribute("assignedOwnerIds", assignedOwnerIds);

        /* ====================================
           4. 답글 관리 (미답변 요청글)
        ==================================== */
        // 4-1. 답글 목록 오프셋 계산
        int boardOffset = (boardPage - 1) * limit;
        // 4-2. 미답변 요청글 목록 조회
        List<BoardDTO> boards = boardService.findUnansweredRequests(boardOffset, limit, filter);
        // 4-3. 미답변 요청글 이 개수 조회
        int boardTotal = boardService.countUnansweredRequests(filter);
        // 4-4. 답글 페이지 수 계산
        int boardTotalPages = (int) Math.ceil((double) boardTotal / limit);
        if (boardTotalPages == 0) boardTotalPages = 1;

        // 4-5. 모델에 답글 데이터 추가
        model.addAttribute("boards", boards);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("boardTotalPages", boardTotalPages);

        /* ====================================
           5. 공지사항 관리
        ==================================== */
        // 5-1. 공지사항 목록 오프셋 계산
        int noticeOffset = (noticePage - 1) * limit;
        // 5-2. 공지사항 목록 조회
        List<BoardDTO> notices = boardService.findAllNotices(noticeOffset, limit);
        // 5-3. 공지사항 이 개수 조회
        int noticeTotal = boardService.countAllNotices();
        // 5-4. 공지사항 페이지 수 계산
        int noticeTotalPages = (int) Math.ceil((double) noticeTotal / limit);
        if (noticeTotalPages == 0) noticeTotalPages = 1;

        // 5-5. 모델에 공지사항 데이터 추가
        model.addAttribute("notices", notices);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("noticeTotalPages", noticeTotalPages);

        // 5-6. 관리자 대시보드 페이지 반환
        return "admin/admin-dashboard";
    }

    /* ====================================
       사용자 역할 변경
    ==================================== */
    // 6. 사용자 역할 변경 (GET 요청)
    @GetMapping("/admin/update-role")
    public String updateUserRole(@RequestParam(name = "id") Long userId,
                                 @RequestParam(name = "role") String role) {
        // 6-1. 사용자 역할 업데이트
        userService.updateUserRole(userId, role);
        // 6-2. 관리자 대시보드 사용자 탭으로 리다이렉트
        return "redirect:/admin/dashboard?tab=user";
    }

    /* ====================================
       식당 오너 지정
    ==================================== */
    // 7. 식당 오너 지정 (GET 요청)
    @GetMapping("/admin/update-owner")
    public String updateRestaurantOwner(@RequestParam(name = "restaurantId") Long restaurantId,
                                        @RequestParam(name = "ownerId", required = false) Long ownerId) {
        // 7-1. 식당 오너 업데이트
        restaurantService.updateOwner(restaurantId, ownerId);
        // 7-2. 관리자 대시보드 식당 탭으로 리다이렉트
        return "redirect:/admin/dashboard?tab=restaurant";
    }

    /* ====================================
       관리자 기능 - 공지사항 관리
    ==================================== */

    // 8. 공지사항 작성 폼 표시 (GET 요청, 관리자만)
    @GetMapping("/admin/notice/create")
    public String showNoticeCreateForm(HttpSession session, Model model) {
        // 8-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 8-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        // 8-3. 관리자 권한 확인
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/";
        }

        // 8-4. 빈 공지사항 객체 생성해서 뷰에 전달
        model.addAttribute("notice", new BoardDTO());

        return "admin/admin-notice-write";
    }

    // 9. 공지사항 수정 폼 표시 (GET 요청, 관리자만)
    @GetMapping("/admin/notice/edit/{id}")
    public String showNoticeEditForm(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 9-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 9-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        // 9-3. 관리자 권한 확인
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/";
        }

        // 9-4. 기존 공지사항 조회
        BoardDTO notice = boardService.findById(id);
        // 9-5. 공지사항이 존재하지 않으면 대시보드로 이동
        if (notice == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 공지사항입니다.");
            return "redirect:/admin/dashboard?tab=notice";
        }

        // 9-6. 모델에 공지사항 정보 담기
        model.addAttribute("notice", notice);

        return "admin/admin-notice-write";
    }

    // 10. 공지사항 저장 (작성/수정, POST 요청)
    @PostMapping("/admin/notice/save")
    public String saveNotice(
            @ModelAttribute BoardDTO notice,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 10-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 10-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        // 10-3. 관리자 권한 확인
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "관리자만 공지사항을 작성할 수 있습니다.");
            return "redirect:/";
        }

        try {
            // 10-4. 공지사항 작성자 ID 설정
            notice.setUserId(user.getId());

            // 10-5. 공지사항 작성/수정 구분
            if (notice.getId() == null || notice.getId() == 0) {
                // 10-5-1. 신규 공지사항 저장
                boardService.insertBoard(notice);
                // 10-5-2. 성공 메시지 플래시 어트리뷰트
                redirectAttributes.addFlashAttribute("successMessage", "공지사항이 작성되었습니다.");
            } else {
                // 10-5-3. 기존 공지사항 수정
                boardService.updateBoard(notice);
                // 10-5-4. 성공 메시지 플래시 어트리뷰트
                redirectAttributes.addFlashAttribute("successMessage", "공지사항이 수정되었습니다.");
            }

            // 10-6. 관리자 대시보드 공지사항 탭으로 리다이렉트
            return "redirect:/admin/dashboard?tab=notice";
        } catch (Exception e) {
            // 10-7. 오류 메시지 플래시 어트리뷰트
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 저장 중 오류가 발생했습니다.");
            return "redirect:/admin/notice/create";
        }
    }

    // 11. 공지사항 삭제 (GET 요청, 관리자만)
    @GetMapping("/admin/notice/delete/{id}")
    public String deleteNotice(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 11-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 11-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        // 11-3. 관리자 권한 확인
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제할 권한이 없습니다.");
            return "redirect:/";
        }

        try {
            // 11-4. 공지사항 삭제 처리
            boardService.deleteBoard(id);
            // 11-5. 성공 메시지 플래시 어트리뷰트
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            // 11-6. 오류 메시지 플래시 어트리뷰트
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 삭제 중 오류가 발생했습니다.");
        }

        // 11-7. 관리자 대시보드 공지사항 탭으로 리다이렉트
        return "redirect:/admin/dashboard?tab=notice";
    }

    /* ====================================
       관리자 기능 - 미답변 요청 관리
    ==================================== */

    // 12. 미답변 요청 조회 (GET 요청, 관리자만)
    @GetMapping("/admin/requests")
    public String getAdminRequests(
            HttpSession session,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        // 12-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 12-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        // 12-3. 관리자 권한 확인
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/";
        }

        // 12-4. 한 페이지에 표시할 요청 개수
        int limit = 10;
        // 12-5. 페이지네이션 오프셋 계산
        int offset = (page - 1) * limit;

        // 12-6. 미답변 요청 조회
        List<BoardDTO> requests = boardService.findUnansweredRequests(offset, limit, filter);
        // 12-7. 미답변 요청 총 개수 조회
        int totalCount = boardService.countUnansweredRequests(filter);
        // 12-8. 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / limit);

        // 12-9. 모델에 미답변 요청 담기
        model.addAttribute("requests", requests);
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);

        return "admin/admin-requests";
    }

    /* ====================================
       관리자 기능 - 답글 제출
    ==================================== */

    // 13. 관리자 답글 제출 (POST 요청, 관리자만)
    @PostMapping("/admin/reply")
    public String submitAdminReply(
            HttpSession session,
            @ModelAttribute BoardDTO reply,
            RedirectAttributes redirectAttributes) {

        // 13-1. 세션에서 사용자 정보 조회
        UserDTO user = (UserDTO) session.getAttribute("user");
        // 13-2. 로그인하지 않은 사용자 차단
        if (user == null) {
            return "redirect:/user/login";
        }

        try {
            // 13-3. 관리자 권한 확인
            if (!"ROLE_ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "관리자만 답글을 작성할 수 있습니다.");
                return "redirect:/admin/requests";
            }

            // 13-4. 부모 게시글 조회
            BoardDTO parentPost = boardService.findById(reply.getParentId());

            // 13-5. QUESTION, SUGGESTION 카테고리만 관리자 답글 가능
            if (!"QUESTION".equals(parentPost.getCategory()) && !"SUGGESTION".equals(parentPost.getCategory())) {
                redirectAttributes.addFlashAttribute("errorMessage", "답글을 작성할 수 없는 게시글입니다.");
                return "redirect:/admin/requests";
            }

            reply.setCategory(parentPost.getCategory());
            
            // 13-6. 답글 작성자 ID 설정 (관리자)
            reply.setUserId(user.getId());
            // 13-7. 관리자 답글 저장
            boardService.insertAdminReply(reply);
            // 13-8. 성공 메시지 플래시 어트리뷰트
            redirectAttributes.addFlashAttribute("successMessage", "답글이 작성되었습니다.");

            // 13-9. 미답변 요청 조회로 리다이렉트
            return "redirect:/admin/dashboard?tab=board";
        } catch (Exception e) {
            // 13-10. 오류 메시지 플래시 어트리뷰트
            redirectAttributes.addFlashAttribute("errorMessage", "답글 작성 중 오류가 발생했습니다.");
            return "redirect:/admin/dashboard?tab=board";
        }
    }
}