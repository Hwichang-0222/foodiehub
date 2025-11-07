package org.embed.mappertest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
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
class BoardMapperTest {

    @Autowired
    private BoardMapper boardMapper;

    private static Long testUserId = 41L;
    private static Long normalBoardId;
    private static Long noticeBoardId;

    @BeforeAll
    static void initAll() {
        System.out.println("\n===== BoardMapper 통합 테스트 시작 =====\n");
    }

    /* ============================================
       게시글 등록
    ============================================ */

    @Test
    @Order(1)
    void testInsertBoard() {
        BoardDTO board = new BoardDTO();
        board.setUserId(testUserId);
        board.setTitle("일반글 테스트");
        board.setContent("일반 게시글 등록 테스트입니다.");
        board.setCategory("QUESTION");
        board.setIsPrivate(false);

        int result = boardMapper.insertBoard(board);
        assertEquals(1, result);
        assertNotNull(board.getId());
        normalBoardId = board.getId();

        System.out.println("[1] 일반 게시글 등록 - ID: " + normalBoardId);
    }

    @Test
    @Order(2)
    void testInsertNoticeBoard() {
        BoardDTO notice = new BoardDTO();
        notice.setUserId(testUserId);
        notice.setTitle("공지사항 테스트");
        notice.setContent("전체 공지사항 테스트입니다.");
        notice.setCategory("NOTICE");

        int result = boardMapper.insertBoard(notice);
        assertEquals(1, result);
        assertNotNull(notice.getId());
        noticeBoardId = notice.getId();

        System.out.println("[2] 공지 게시글 등록 - ID: " + noticeBoardId);
    }

    /* ============================================
       공지글 조회
    ============================================ */

    @Test
    @Order(3)
    void testFindNoticesByCategory() {
        List<BoardDTO> notices = boardMapper.findNoticesByCategory("GENERAL");
        assertTrue(notices.size() >= 0);
        System.out.println("[3] 공지글 조회(카테고리: GENERAL) - " + notices.size() + "개");
    }

    /* ============================================
       일반글 조회
    ============================================ */

    @Test
    @Order(4)
    void testFindNormalPostsByCategory() {
        List<BoardDTO> posts = boardMapper.findNormalPostsByCategory("GENERAL", 0, 5);
        assertNotNull(posts);
        System.out.println("[4] 일반글 조회(카테고리: GENERAL, 페이지 1) - " + posts.size() + "개");
    }

    @Test
    @Order(5)
    void testCountNormalPostsByCategory() {
        int count = boardMapper.countNormalPostsByCategory("GENERAL");
        assertTrue(count >= 1);
        System.out.println("[5] 일반글 개수 - " + count + "개");
    }

    /* ============================================
       게시글 상세 조회
    ============================================ */

    @Test
    @Order(6)
    void testFindById() {
        BoardDTO board = boardMapper.findById(normalBoardId);
        assertNotNull(board);
        System.out.println("[6] 단일 게시글 조회 - 제목: " + board.getTitle());
    }

    /* ============================================
       조회수 증가
    ============================================ */

    @Test
    @Order(7)
    void testIncreaseViewCount() {
        BoardDTO before = boardMapper.findById(normalBoardId);
        int beforeCount = before.getViewCount();

        int updated = boardMapper.increaseViewCount(normalBoardId);
        assertEquals(1, updated);

        BoardDTO after = boardMapper.findById(normalBoardId);
        assertEquals(beforeCount + 1, after.getViewCount());

        System.out.println("[7] 조회수 증가 - " + beforeCount + " → " + after.getViewCount());
    }

    /* ============================================
       사용자별 조회
    ============================================ */

    @Test
    @Order(8)
    void testFindByUserId() {
        List<BoardDTO> list = boardMapper.findByUserId(testUserId);
        assertTrue(list.size() > 0);
        System.out.println("[8] 사용자별 게시글 조회 - " + list.size() + "개");
    }

    @Test
    @Order(9)
    void testCountByUserId() {
        int count = boardMapper.countByUserId(testUserId);
        assertTrue(count > 0);
        System.out.println("[9] 사용자별 게시글 개수 - " + count + "개");
    }

    @Test
    @Order(10)
    void testFindPagedByUserId() {
        List<BoardDTO> list = boardMapper.findPagedByUserId(testUserId, 0, 5);
        assertNotNull(list);
        System.out.println("[10] 사용자별 페이지 게시글 조회 - " + list.size() + "개");
    }

    /* ============================================
       게시글 수정
    ============================================ */

    @Test
    @Order(11)
    void testUpdateBoard() {
        BoardDTO board = boardMapper.findById(normalBoardId);
        board.setTitle("수정된 게시글");
        board.setContent("수정된 내용입니다.");
        int result = boardMapper.updateBoard(board);
        assertEquals(1, result);

        BoardDTO updated = boardMapper.findById(normalBoardId);
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
        reply.setTitle("답");
        reply.setContent("관리자 답변입니다.");

        int result = boardMapper.insertAdminReply(reply);
        assertEquals(1, result);

        System.out.println("[12] 관리자 답글 등록 - 부모글ID: " + normalBoardId);
    }

    /* ============================================
       게시글 검색
    ============================================ */

    @Test
    @Order(13)
    void testSearchBoard() {
        List<BoardDTO> results = boardMapper.searchBoard("GENERAL", "테스트", 0, 10);
        assertNotNull(results);
        System.out.println("[13] 게시글 검색(키워드: 테스트) - " + results.size() + "개");
    }

    @Test
    @Order(14)
    void testCountSearchBoards() {
        int count = boardMapper.countSearchBoards("GENERAL", "테스트");
        assertTrue(count >= 0);
        System.out.println("[14] 검색 결과 개수 - " + count + "개");
    }

    /* ============================================
       관리자 기능
    ============================================ */

    @Test
    @Order(15)
    void testFindAllNotices() {
        List<BoardDTO> notices = boardMapper.findAllNotices(0, 10);
        assertNotNull(notices);
        System.out.println("[15] 모든 공지사항 조회 - " + notices.size() + "개");
    }

    @Test
    @Order(16)
    void testCountAllNotices() {
        int count = boardMapper.countAllNotices();
        assertTrue(count >= 0);
        System.out.println("[16] 공지사항 총 개수 - " + count + "개");
    }

    @Test
    @Order(17)
    void testFindUnansweredRequests() {
        List<BoardDTO> unanswered = boardMapper.findUnansweredRequests(0, 10, "all");
        assertNotNull(unanswered);
        System.out.println("[17] 답글 미등록 요청글 조회 - " + unanswered.size() + "개");
    }

    @Test
    @Order(18)
    void testCountUnansweredRequests() {
        int count = boardMapper.countUnansweredRequests("all");
        assertTrue(count >= 0);
        System.out.println("[18] 답글 미등록 요청글 개수 - " + count + "개");
    }

    /* ============================================
       게시글 삭제
    ============================================ */

    @Test
    @Order(19)
    void testDeleteBoards() {
        int deleteNormal = boardMapper.deleteBoard(normalBoardId);
        int deleteNotice = boardMapper.deleteBoard(noticeBoardId);
        assertEquals(1, deleteNormal);
        assertEquals(1, deleteNotice);
        System.out.println("[19] 게시글 삭제 - 일반글(" + normalBoardId + "), 공지글(" + noticeBoardId + ")");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n===== BoardMapper 통합 테스트 종료 =====\n");
    }
}