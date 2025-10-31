package org.embed.service;

import java.util.List;
import org.embed.dto.BoardDTO;

public interface BoardService {

    /* ------------------------------
       공지글 조회
    ------------------------------ */
    // 1. 탭별 공지글 (전역공지 + 탭별 공지)
    List<BoardDTO> findNoticesByCategory(String category);

    /* ------------------------------
       일반글 조회
    ------------------------------ */
    // 2. 탭별 일반글 (페이지네이션)
    List<BoardDTO> findNormalPostsByCategory(String category, int offset, int limit);

    // 3. 일반글 총 개수
    int countNormalPostsByCategory(String category);

    /* ------------------------------
       검색
    ------------------------------ */
    // 4. 게시글 검색 (탭별 카테고리 + 키워드)
    List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit);

    // 5. 검색 결과 개수
    int countSearchBoards(String category, String keyword);

    /* ------------------------------
       기본 CRUD
    ------------------------------ */
    // 6. 단일 게시글 조회
    BoardDTO findById(Long id);

    // 7. 게시글 등록
    int insertBoard(BoardDTO board);

    // 8. 게시글 수정
    int updateBoard(BoardDTO board);

    // 9. 게시글 삭제
    int deleteBoard(Long id);

    // 10. 조회수 증가
    int increaseViewCount(Long id);

    /* ------------------------------
       관리자 답변
    ------------------------------ */
    // 11. 관리자 답변 등록
    int insertAdminReply(BoardDTO reply);

    /* ------------------------------
       사용자별 조회 (마이페이지)
    ------------------------------ */
    // 12. 사용자별 게시글 총 개수
    int countByUserId(Long userId);

    // 13. 사용자별 게시글 목록 (페이지네이션)
    List<BoardDTO> findPagedByUserId(Long userId, int offset, int limit);

    /* ------------------------------
       관리자 기능
    ------------------------------ */
    // 14. 공지사항 목록
    List<BoardDTO> findAllNotices(int offset, int limit);

    // 15. 공지사항 총 개수
    int countAllNotices();

    // 16. 답글 미등록 요청 게시글 조회
    List<BoardDTO> findUnansweredRequests(int offset, int limit, String filter);

    // 17. 답글 미등록 요청 게시글 총 개수
    int countUnansweredRequests(String filter);
}