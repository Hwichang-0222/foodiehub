package org.embed.mappertest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardMapperTest {

    @Autowired
    private BoardMapper boardMapper;

    private static Long testUserId = 3L; // DB 내 존재하는 테스트 유저
    private static Long generalBoardId;  // 일반글
    private static Long noticeBoardId;   // 공지글

    @BeforeAll
    static void initAll() {
        System.out.println("\n===== BoardMapper 통합 테스트 시작 =====\n");
    }

    // 1. 일반 게시글 등록
    @Test
    @Order(1)
    void testInsertBoard() {
        BoardDTO board = new BoardDTO();
        board.setUserId(testUserId);
        board.setTitle("일반글 테스트");
        board.setContent("일반 게시글 등록 테스트입니다.");
        board.setCategory("GENERAL");
        board.setIsPrivate(false);

        int result = boardMapper.insertBoard(board);
        assertEquals(1, result);
        generalBoardId = board.getId();
        assertNotNull(generalBoardId);

        System.out.println("1. 일반 게시글 등록 완료 (ID=" + generalBoardId + ")");
    }

    // 2. 공지 게시글 등록
    @Test
    @Order(2)
    void testInsertNoticeBoard() {
        BoardDTO notice = new BoardDTO();
        notice.setUserId(testUserId);
        notice.setTitle("전역 공지 테스트");
        notice.setContent("이것은 전체 공지글 테스트입니다.");
        notice.setCategory("NOTICE");

        int result = boardMapper.insertBoard(notice);
        assertEquals(1, result);
        noticeBoardId = notice.getId();

        System.out.println("2. 공지 게시글 등록 완료 (ID=" + noticeBoardId + ")");
    }

    // 3. 공지글 조회 (탭별)
    @Test
    @Order(3)
    void testFindNoticesByCategory() {
        List<BoardDTO> notices = boardMapper.findNoticesByCategory("GENERAL");
        assertTrue(notices.size() > 0);
        System.out.println("3. 공지글 조회 수 = " + notices.size());
        notices.forEach(b -> System.out.println("   공지: " + b.getTitle() + " [" + b.getCategory() + "]"));
    }

    // 4. 일반글 조회 (페이지네이션)
    @Test
    @Order(4)
    void testFindNormalPostsByCategory() {
        List<BoardDTO> posts = boardMapper.findNormalPostsByCategory("GENERAL", 0, 5);
        assertNotNull(posts);
        System.out.println("4. 일반글 조회 수 = " + posts.size());
        posts.forEach(p -> System.out.println("   일반글: " + p.getTitle()));
    }

    // 5. 일반글 개수 조회
    @Test
    @Order(5)
    void testCountNormalPostsByCategory() {
        int count = boardMapper.countNormalPostsByCategory("GENERAL");
        assertTrue(count >= 1);
        System.out.println("5. 일반글 총 개수 = " + count);
    }

    // 6. 단일 게시글 조회
    @Test
    @Order(6)
    void testFindById() {
        BoardDTO board = boardMapper.findById(generalBoardId);
        assertNotNull(board);
        System.out.println("6. 단일 게시글 조회: " + board.getTitle());
    }

    // 7. 조회수 증가
    @Test
    @Order(7)
    void testIncreaseViewCount() {
        BoardDTO before = boardMapper.findById(generalBoardId);
        int beforeCount = before.getViewCount();

        int updated = boardMapper.increaseViewCount(generalBoardId);
        assertEquals(1, updated);

        BoardDTO after = boardMapper.findById(generalBoardId);
        assertEquals(beforeCount + 1, after.getViewCount());

        System.out.println("7. 조회수 증가 전: " + beforeCount + " → 증가 후: " + after.getViewCount());
    }

    // 8. 사용자별 게시글 조회
    @Test
    @Order(8)
    void testFindByUserId() {
        List<BoardDTO> list = boardMapper.findByUserId(testUserId);
        assertTrue(list.size() > 0);
        System.out.println("8. 사용자 작성글 수 = " + list.size());
    }

    // 9. 게시글 수정
    @Test
    @Order(9)
    void testUpdateBoard() {
        BoardDTO board = boardMapper.findById(generalBoardId);
        board.setTitle("수정된 게시글 제목");
        board.setContent("수정된 내용입니다.");
        int result = boardMapper.updateBoard(board);
        assertEquals(1, result);

        BoardDTO updated = boardMapper.findById(generalBoardId);
        assertEquals("수정된 게시글 제목", updated.getTitle());
        System.out.println("9. 게시글 수정 완료: " + updated.getTitle());
    }

    // 10. 관리자 답글 등록
    @Test
    @Order(10)
    void testInsertAdminReply() {
        BoardDTO reply = new BoardDTO();
        reply.setUserId(testUserId);
        reply.setParentId(generalBoardId);
        reply.setContent("관리자 답변 테스트 내용입니다.");

        int result = boardMapper.insertAdminReply(reply);
        assertEquals(1, result);

        System.out.println("10. 관리자 답변 등록 완료 (parent_id=" + generalBoardId + ")");
    }

    // 11. 검색 테스트
    @Test
    @Order(11)
    void testSearchBoard() {
        List<BoardDTO> results = boardMapper.searchBoard("GENERAL", "테스트", 0, 10);
        assertNotNull(results);
        System.out.println("11. 검색 결과 수 = " + results.size());
        results.forEach(r -> System.out.println("   검색 결과: " + r.getTitle() + " (" + r.getCategory() + ")"));
    }

    // 12. 게시글 삭제
    @Test
    @Order(12)
    void testDeleteBoards() {
        int deleteNormal = boardMapper.deleteBoard(generalBoardId);
        int deleteNotice = boardMapper.deleteBoard(noticeBoardId);
        assertEquals(1, deleteNormal);
        assertEquals(1, deleteNotice);
        System.out.println("12. 테스트 게시글 삭제 완료 (ID=" + generalBoardId + ", " + noticeBoardId + ")");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n===== BoardMapper 통합 테스트 종료 =====\n");
    }
}
