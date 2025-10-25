package org.embed.service;

import java.util.List;
import java.util.Map;
import org.embed.dto.BoardDTO;

public interface BoardService {

    // 1. 공지글 + 일반글 통합 조회 (페이지네이션)
    Map<String, Object> getBoardPage(String category, int page, int limit);

    // 2. 단일 게시글 조회 (조회수 증가 포함)
    BoardDTO getBoardDetail(Long id);

    // 3. 게시글 등록
    int insertBoard(BoardDTO board);

    // 4. 게시글 수정
    int updateBoard(BoardDTO board);

    // 5. 게시글 삭제
    int deleteBoard(Long id);

    // 6. 사용자 작성글 조회
    List<BoardDTO> findByUserId(Long userId);

    // 7. 게시판 검색 (카테고리 + 키워드)
    Map<String, Object> searchBoards(String category, String keyword, int page, int limit);

    // 8. 관리자 답변 등록
    int insertAdminReply(BoardDTO reply);
}
