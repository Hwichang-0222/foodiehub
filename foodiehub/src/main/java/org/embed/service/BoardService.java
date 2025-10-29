package org.embed.service;

import java.util.List;
import org.embed.dto.BoardDTO;

public interface BoardService {

    // 1. 탭별 공지글 (전역공지 + 탭별 공지)
    List<BoardDTO> findNoticesByCategory(String category);

    // 2. 탭별 일반글 (페이지네이션)
    List<BoardDTO> findNormalPostsByCategory(String category, int page, int size);

    // 3. 일반글 총 개수 (페이지 계산용)
    int countNormalPostsByCategory(String category);

    // 4. 단일 게시글 조회
    BoardDTO findById(Long id);

    // 5. 사용자 작성글 목록
    List<BoardDTO> findByUserId(Long userId);

    // 6. 게시글 등록
    int insertBoard(BoardDTO board);

    // 7. 게시글 수정
    int updateBoard(BoardDTO board);

    // 8. 게시글 삭제
    int deleteBoard(Long id);

    // 9. 관리자 답변 등록
    int insertAdminReply(BoardDTO reply);

    // 10. 게시글 검색 (탭별 카테고리 + 키워드)
    List<BoardDTO> searchBoard(String category, String keyword, int page, int size);

    // 11. 검색 결과 개수
    int countSearchBoards(String category, String keyword);

    // 12. 조회수 증가
    int increaseViewCount(Long id);

    // 13. 사용자별 게시글 총 개수
    int countByUserId(Long userId);

    // 14. 사용자별 게시글 목록 (페이지네이션)
    List<BoardDTO> findPagedByUserId(Long userId, int page, int size);

    // 15. 관리자용 공지사항 목록
    List<BoardDTO> findAllNotices(int page, int size);

    // 16. 관리자용 공지사항 총 개수
    int countAllNotices();

    // 17. 관리자용 답글 미등록 요청 게시글 조회
    List<BoardDTO> findUnansweredRequests(int page, int size, String filter);

    // 18. 관리자용 답글 미등록 요청 게시글 총 개수
    int countUnansweredRequests(String filter);
}
