package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.service.BoardService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    private static Long testUserId = 41L;
    private static Long normalBoardId;

    @BeforeAll
    static void initAll() {
        System.out.println("\n===== BoardService 통합 테스트 시작 =====\n");
    }

    /* ============================================
       공지글 관련 테스트
    ============================================ */

    @Test
    @Order(1)
    void testFindNoticesByCategory() {
        List<BoardDTO> notices = boardService.findNoticesByCategory("GENERAL");
        assertNotNull(notices);
        System.out.println("[1] 공지글 조회(카테고리: GENERAL) - " + notices.size() + "개");
    }

    @Test
    @Order(2)
    void testFindAllNotices() {
        List<BoardDTO> notices = boardService.findAllNotices(0, 10);
        assertNotNull(notices);
        System.out.println("[2] 모든 공지사항 조회(페이지 1) - " + notices.size() + "개");
    }

    @Test
    @Order(3)
    void testCountAllNotices() {
        int count = boardService.countAllNotices();
        assertTrue(count >= 0);
        System.out.println("[3] 공지사항 총 개수 - " + count + "개");
    }

    /* ============================================
       일반글 등록
    ============================================ */

    @Test
    @Order(4)
    void testInsertBoard() {
        BoardDTO board = new BoardDTO();
        board.setUserId(testUserId);
        board.setTitle("일반글 테스트");
        board.setContent("게시글 테스트입니다.");
        board.setCategory("GENERAL");
        board.setIsPrivate(false);

        int result = boardService.insertBoard(board);
        assertEquals(1, result);
        assertNotNull(board.getId());
        normalBoardId = board.getId();

        System.out.println("[4] 일반글 등록 - ID: " + normalBoardId);
    }

    /* ============================================
       일반글 조회
    ============================================ */

    @Test
    @Order(5)
    void testFindNormalPostsByCategory() {
        List<BoardDTO> posts = boardService.findNormalPostsByCategory("GENERAL", 0, 5);
        assertNotNull(posts);
        System.out.println("[5] 일반글 조회(카테고리: GENERAL, 페이지 1) - " + posts.size() + "개");
    }

    @Test
    @Order(6)
    void testCountNormalPostsByCategory() {
        int count = boardService.countNormalPostsByCategory("GENERAL");
        assertTrue(count >= 1);
        System.out.println("[6] 일반글 총 개수(카테고리: GENERAL) - " + count + "개");
    }

    /* ============================================
       게시글 상세 조회
    ============================================ */

    @Test
    @Order(7)
    void testFindById() {
        BoardDTO board = boardService.findById(normalBoardId);
        assertNotNull(board);
        assertEquals("일반글 테스트", board.getTitle());
        System.out.println("[7] 게시글 조회 - 제목: " + board.getTitle());
    }

    /* ============================================
       조회수 테스트
    ============================================ */

    @Test
    @Order(8)
    void testIncreaseViewCount() {
        BoardDTO before = boardService.findById(normalBoardId);
        int beforeCount = before.getViewCount();

        int result = boardService.increaseViewCount(normalBoardId);
        assertEquals(1, result);

        BoardDTO after = boardService.findById(normalBoardId);
        assertEquals(beforeCount + 1, after.getViewCount());

        System.out.println("[8] 조회수 증가 - " + beforeCount + " → " + after.getViewCount());
    }

    /* ============================================
       게시글 검색
    ============================================ */

    @Test
    @Order(9)
    void testSearchBoard() {
        List<BoardDTO> results = boardService.searchBoard("GENERAL", "테스트", 0, 10);
        assertNotNull(results);
        System.out.println("[9] 게시글 검색(키워드: 테스트) - " + results.size() + "개");
    }

    @Test
    @Order(10)
    void testCountSearchBoards() {
        int count = boardService.countSearchBoards("GENERAL", "테스트");
        assertTrue(count >= 0);
        System.out.println("[10] 검색 결과 개수(키워드: 테스트) - " + count + "개");
    }

    /* ============================================
       게시글 수정
    ============================================ */

    @Test
    @Order(11)
    void testUpdateBoard() {
        BoardDTO board = boardService.findById(normalBoardId);
        board.setTitle("수정된 게시글");
        board.setContent("수정된 내용입니다.");

        int result = boardService.updateBoard(board);
        assertEquals(1, result);

        BoardDTO updated = boardService.findById(normalBoardId);
        assertEquals("수정된 게시글", updated.getTitle());
        System.out.println("[11] 게시글 수정 - 변경된 제목: " + updated.getTitle());
    }

    /* ============================================
       관리자 답글
    ============================================ */

    @Test
    @Order(12)
    void testInsertAdminReply() {
        BoardDTO reply = new BoardDTO();
        reply.setUserId(testUserId);
        reply.setParentId(normalBoardId);
        reply.setTitle("reply");
        reply.setContent("관리자 답변입니다.");

        int result = boardService.insertAdminReply(reply);
        assertEquals(1, result);

        System.out.println("[12] 관리자 답글 등록 - 부모글ID: " + normalBoardId);
    }

    /* ============================================
       사용자별 게시글 (마이페이지)
    ============================================ */

    @Test
    @Order(13)
    void testCountByUserId() {
        int count = boardService.countByUserId(testUserId);
        assertTrue(count > 0);
        System.out.println("[13] 사용자별 게시글 개수(사용자ID: " + testUserId + ") - " + count + "개");
    }

    @Test
    @Order(14)
    void testFindPagedByUserId() {
        List<BoardDTO> boards = boardService.findPagedByUserId(testUserId, 0, 5);
        assertNotNull(boards);
        System.out.println("[14] 사용자별 게시글 조회(페이지 1) - " + boards.size() + "개");
    }

    /* ============================================
       답글 미등록 요청 게시글 (관리자)
    ============================================ */

    @Test
    @Order(15)
    void testFindUnansweredRequests() {
        List<BoardDTO> unanswered = boardService.findUnansweredRequests(0, 10, "all");
        assertNotNull(unanswered);
        System.out.println("[15] 답글 미등록 요청글 조회(필터: all) - " + unanswered.size() + "개");
    }

    @Test
    @Order(16)
    void testCountUnansweredRequests() {
        int count = boardService.countUnansweredRequests("all");
        assertTrue(count >= 0);
        System.out.println("[16] 답글 미등록 요청글 개수(필터: all) - " + count + "개");
    }

    /* ============================================
       게시글 삭제
    ============================================ */

    @Test
    @Order(17)
    void testDeleteBoard() {
        int result = boardService.deleteBoard(normalBoardId);
        assertEquals(1, result);
        System.out.println("[17] 게시글 삭제 - ID: " + normalBoardId);
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n===== BoardService 통합 테스트 종료 =====\n");
    }
}