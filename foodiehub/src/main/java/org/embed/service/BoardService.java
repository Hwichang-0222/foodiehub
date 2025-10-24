package org.embed.service;

import org.embed.dto.BoardDTO;
import java.util.List;

public interface BoardService {

    // 1. 전체 게시글 조회 (공지 우선 + 페이징)
    List<BoardDTO> findAll(int offset, int limit);

    // 2. 단일 게시글 조회
    BoardDTO findById(Long id);

    // 3. 사용자 작성글 조회 (마이페이지용)
    List<BoardDTO> findByUserId(Long userId);

    // 4. 게시글 작성
    int insertBoard(BoardDTO board);

    // 5. 게시글 수정
    int updateBoard(BoardDTO board);

    // 6. 게시글 삭제
    int deleteBoard(Long id);

    // 7. 관리자 답변 등록 (parent_id 설정)
    int insertAdminReply(BoardDTO reply);

    // 8. 게시글 검색 (카테고리, 제목, 작성자)
    List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit);

    // 9. 전체 게시글 수 (페이지네이션용)
    int countBoards(String category, String keyword);
}
