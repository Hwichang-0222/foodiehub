package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import org.embed.dto.BoardDTO;
import org.embed.service.BoardService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    private static Long adminUserId = 3L; // 관리자 ID
    private static Long createdBoardId;

    @BeforeAll
    static void initAll() {
        System.out.println("\n===== BoardService 통합 테스트 시작 =====\n");
    }

    // 1. 게시글 등록
    @Test
    @Order(1)
    void testInsertBoard() {
        BoardDTO board = new BoardDTO();
        board.setUserId(adminUserId);
        board.setTitle("서비스 게시글 등록 테스트");
        board.setContent("BoardService에서 Mapper 연결 테스트");
        board.setCategory("GENERAL");
        board.setIsPrivate(false);

        int result = boardService.insertBoard(board);
        assertEquals(1, result);
        assertNotNull(board.getId());
        createdBoardId = board.getId();

        System.out.println("1. 게시글 등록 완료 (ID=" + createdBoardId + ")");
    }

    // 2. 게시판 목록 (공지 + 일반글)
    @Test
    @Order(2)
    void testGetBoardPage() {
        Map<String, Object> data = boardService.getBoardPage("GENERAL", 1, 5);
        List<BoardDTO> notices = (List<BoardDTO>) data.get("notices");
        List<BoardDTO> posts = (List<BoardDTO>) data.get("posts");

        assertNotNull(data);
        System.out.println("2. 공지 수 = " + notices.size() + ", 일반글 수 = " + posts.size());
    }

    // 3. 단일 게시글 조회 (조회수 증가 확인)
    @Test
    @Order(3)
    void testGetBoardDetail() {
        BoardDTO before = boardService.getBoardDetail(createdBoardId);
        int beforeView = before.getViewCount();

        BoardDTO after = boardService.getBoardDetail(createdBoardId);
        assertEquals(beforeView + 1, after.getViewCount());

        System.out.println("3. 조회수 증가 확인: " + beforeView + " → " + after.getViewCount());
    }

    // 4. 게시글 수정
    @Test
    @Order(4)
    void testUpdateBoard() {
        BoardDTO board = boardService.getBoardDetail(createdBoardId);
        board.setTitle("서비스 수정 테스트 제목");
        board.setContent("서비스 수정 테스트 내용");

        int result = boardService.updateBoard(board);
        assertEquals(1, result);

        BoardDTO updated = boardService.getBoardDetail(createdBoardId);
        assertEquals("서비스 수정 테스트 제목", updated.getTitle());

        System.out.println("4. 게시글 수정 완료");
    }

    // 5. 사용자별 게시글 목록
    @Test
    @Order(5)
    void testFindByUserId() {
        List<BoardDTO> list = boardService.findByUserId(adminUserId);
        assertTrue(list.size() > 0);
        System.out.println("5. 사용자 작성글 수 = " + list.size());
    }

    // 6. 검색
    @Test
    @Order(6)
    void testSearchBoards() {
        Map<String, Object> map = boardService.searchBoards("GENERAL", "테스트", 1, 10);
        List<BoardDTO> list = (List<BoardDTO>) map.get("list");
        int total = (int) map.get("total");

        assertNotNull(list);
        System.out.println("6. 검색 결과 = " + list.size() + " / 총 " + total);
    }

    // 7. 관리자 답변 등록
    @Test
    @Order(7)
    void testInsertAdminReply() {
        BoardDTO reply = new BoardDTO();
        reply.setUserId(adminUserId);
        reply.setParentId(createdBoardId);
        reply.setContent("관리자 답변 테스트");
        int result = boardService.insertAdminReply(reply);

        assertEquals(1, result);
        System.out.println("7. 관리자 답변 등록 완료");
    }

    // 8. 게시글 삭제
    @Test
    @Order(8)
    void testDeleteBoard() {
        int result = boardService.deleteBoard(createdBoardId);
        assertEquals(1, result);
        System.out.println("8. 게시글 삭제 완료 (ID=" + createdBoardId + ")");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n===== BoardService 통합 테스트 종료 =====\n");
    }
}
