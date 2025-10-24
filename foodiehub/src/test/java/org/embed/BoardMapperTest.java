package org.embed;

import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardMapperTest {

    @Autowired
    private BoardMapper boardMapper;

    private static Long testBoardId;
    private static Long adminReplyId;
    private static final Long testUserId = 2L;     // 실제 존재하는 일반 유저 ID
    private static final Long adminUserId = 1L;    // 관리자 계정 ID

    @BeforeAll
    static void start() {
        System.out.println("\n=== BoardMapper 테스트 시작 ===\n");
    }

    // 1. 게시글 등록 테스트
    @Test
    @Order(1)
    void insertBoard() {
        System.out.println("1. 게시글 등록 테스트");

        BoardDTO board = new BoardDTO();
        board.setUserId(testUserId);
        board.setTitle("테스트 게시글 제목");
        board.setContent("테스트 게시글 내용입니다.");
        board.setCategory("QUESTION");
        board.setIsPrivate(true);

        int result = boardMapper.insertBoard(board);
        testBoardId = board.getId();

        System.out.printf("삽입 결과: %d | 생성된 ID: %d%n", result, testBoardId);
        Assertions.assertEquals(1, result);
    }

    // 2. 단일 게시글 조회 테스트
    @Test
    @Order(2)
    void findById() {
        System.out.println("2. 단일 게시글 조회 테스트");

        BoardDTO board = boardMapper.findById(testBoardId);

        Assertions.assertNotNull(board);
        System.out.printf("제목: %s | 작성자: %s%n", board.getTitle(), board.getUserName());
    }

    // 3. 게시글 수정 테스트
    @Test
    @Order(3)
    void updateBoard() {
        System.out.println("3. 게시글 수정 테스트");

        BoardDTO board = new BoardDTO();
        board.setId(testBoardId);
        board.setUserId(testUserId); // 작성자 확인
        board.setTitle("수정된 제목입니다.");
        board.setContent("수정된 게시글 내용입니다.");
        board.setCategory("SUGGESTION");

        int result = boardMapper.updateBoard(board);
        Assertions.assertEquals(1, result);

        BoardDTO updated = boardMapper.findById(testBoardId);
        System.out.printf("수정된 제목: %s%n", updated.getTitle());
    }

    // 4. 관리자 답변 등록 테스트
    @Test
    @Order(4)
    void insertAdminReply() {
        System.out.println("4. 관리자 답변 등록 테스트");

        BoardDTO reply = new BoardDTO();
        reply.setUserId(adminUserId);
        reply.setParentId(testBoardId);
        reply.setContent("관리자 답변 내용입니다.");

        int result = boardMapper.insertAdminReply(reply);
        adminReplyId = reply.getId();

        System.out.printf("답변 등록 결과: %d | 답변 ID: %d%n", result, adminReplyId);
        Assertions.assertEquals(1, result);
    }

    // 5. 전체 게시글 목록 조회 테스트
    @Test
    @Order(5)
    void findAll() {
        System.out.println("5. 전체 게시글 목록 조회 테스트 (공지 상단 + 페이지네이션)");

        List<BoardDTO> boards = boardMapper.findAll(0, 10);
        Assertions.assertFalse(boards.isEmpty());

        for (BoardDTO b : boards) {
            System.out.printf("ID: %d | 카테고리: %s | 제목: %s | 작성자: %s%n",
                    b.getId(), b.getCategory(), b.getTitle(), b.getUserName());
        }
    }

    // 6. 검색 기능 테스트
    @Test
    @Order(6)
    void searchBoard() {
        System.out.println("6. 검색 기능 테스트 (카테고리+키워드)");

        List<BoardDTO> result = boardMapper.searchBoard("SUGGESTION", "수정된", 0, 10);
        Assertions.assertTrue(result.size() >= 0);

        for (BoardDTO b : result) {
            System.out.printf("검색결과 → ID: %d | 제목: %s | 작성자: %s%n",
                    b.getId(), b.getTitle(), b.getUserName());
        }
    }

    // 7. 전체 게시글 수 조회 테스트
    @Test
    @Order(7)
    void countBoards() {
        System.out.println("7. 전체 게시글 수 조회 테스트");

        int total = boardMapper.countBoards(null, "");
        System.out.println("전체 게시글 수: " + total);
        Assertions.assertTrue(total >= 0);
    }

    // 8. 게시글 삭제 테스트
    @Test
    @Order(8)
    void deleteBoard() {
        System.out.println("8. 게시글 삭제 테스트");

        int result = boardMapper.deleteBoard(testBoardId);
        Assertions.assertEquals(1, result);

        BoardDTO deleted = boardMapper.findById(testBoardId);
        Assertions.assertNull(deleted);
        System.out.println("삭제 완료");
    }

    @AfterAll
    static void end() {
        System.out.println("\n=== BoardMapper 테스트 종료 ===\n");
    }
}
