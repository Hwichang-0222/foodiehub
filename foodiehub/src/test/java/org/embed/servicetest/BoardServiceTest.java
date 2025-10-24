package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.service.BoardService;
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

    private static Long testBoardId;
    private static Long testUserId = 1L; // DB에 존재하는 사용자 ID

    @Test
    @Order(1)
    void testInsertBoard() {
        BoardDTO board = new BoardDTO();
        board.setUserId(testUserId);
        board.setTitle("서비스 테스트 게시글");
        board.setContent("서비스 테스트용 게시글입니다.");
        board.setCategory("GENERAL");
        board.setIsPrivate(false);

        int result = boardService.insertBoard(board);
        assertEquals(1, result);
        assertNotNull(board.getId());
        testBoardId = board.getId();

        System.out.println("1. insertBoard: id=" + testBoardId);
    }

    @Test
    @Order(2)
    void testFindById() {
        BoardDTO board = boardService.findById(testBoardId);
        assertNotNull(board);
        assertEquals("서비스 테스트 게시글", board.getTitle());
        System.out.println("2. findById: " + board.getTitle());
    }

    @Test
    @Order(3)
    void testUpdateBoard() {
        BoardDTO board = boardService.findById(testBoardId);
        board.setTitle("수정된 게시글 제목");
        int result = boardService.updateBoard(board);
        assertEquals(1, result);

        BoardDTO updated = boardService.findById(testBoardId);
        assertEquals("수정된 게시글 제목", updated.getTitle());
        System.out.println("3. updateBoard: " + updated.getTitle());
    }

    @Test
    @Order(4)
    void testFindByUserId() {
        List<BoardDTO> boards = boardService.findByUserId(testUserId);
        assertNotNull(boards);
        System.out.println("4. findByUserId count: " + boards.size());
    }

    @Test
    @Order(5)
    void testInsertAdminReply() {
        BoardDTO reply = new BoardDTO();
        reply.setUserId(1L); // 관리자 ID
        reply.setParentId(testBoardId);
        reply.setTitle("Re: 서비스 테스트 게시글");
        reply.setContent("관리자 답변입니다.");
        reply.setCategory("QUESTION");

        int result = boardService.insertAdminReply(reply);
        assertEquals(1, result);
        System.out.println("5. insertAdminReply 완료");
    }

    @Test
    @Order(6)
    void testFindAll() {
        List<BoardDTO> boards = boardService.findAll(0, 10);
        assertNotNull(boards);
        assertTrue(boards.size() > 0);
        System.out.println("6. findAll count: " + boards.size());
    }

    @Test
    @Order(7)
    void testSearchBoard() {
        List<BoardDTO> result = boardService.searchBoard("GENERAL", "테스트", 0, 10);
        assertNotNull(result);
        System.out.println("7. searchBoard 결과 수: " + result.size());
    }

    @Test
    @Order(8)
    void testCountBoards() {
        int count = boardService.countBoards("GENERAL", "테스트");
        assertTrue(count >= 0);
        System.out.println("8. countBoards 결과: " + count);
    }

    @Test
    @Order(9)
    void testDeleteBoard() {
        int result = boardService.deleteBoard(testBoardId);
        assertEquals(1, result);
        System.out.println("9. deleteBoard 완료");
    }
}
