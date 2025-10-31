package org.embed.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
import org.embed.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    @Autowired
    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    /* ------------------------------
       공지글 조회
    ------------------------------ */
    // 1. 탭별 공지글 (전역공지 + 탭별 공지)
    @Override
    public List<BoardDTO> findNoticesByCategory(String category) {
        return boardMapper.findNoticesByCategory(category);
    }

    /* ------------------------------
       일반글 조회
    ------------------------------ */
    // 2. 탭별 일반글 (페이지네이션)
    @Override
    public List<BoardDTO> findNormalPostsByCategory(String category, int offset, int limit) {
        return boardMapper.findNormalPostsByCategory(category, offset, limit);
    }

    // 3. 일반글 총 개수
    @Override
    public int countNormalPostsByCategory(String category) {
        return boardMapper.countNormalPostsByCategory(category);
    }

    /* ------------------------------
       검색
    ------------------------------ */
    // 4. 게시글 검색 (탭별 카테고리 + 키워드)
    @Override
    public List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit) {
        return boardMapper.searchBoard(category, keyword, offset, limit);
    }

    // 5. 검색 결과 개수
    @Override
    public int countSearchBoards(String category, String keyword) {
        return boardMapper.countSearchBoards(category, keyword);
    }

    /* ------------------------------
       기본 CRUD
    ------------------------------ */
    // 6. 단일 게시글 조회
    @Override
    public BoardDTO findById(Long id) {
        return boardMapper.findById(id);
    }

    // 7. 게시글 등록
    @Override
    public int insertBoard(BoardDTO board) {
        return boardMapper.insertBoard(board);
    }

    // 8. 게시글 수정
    @Override
    public int updateBoard(BoardDTO board) {
        return boardMapper.updateBoard(board);
    }

    // 9. 게시글 삭제
    @Override
    public int deleteBoard(Long id) {
        return boardMapper.deleteBoard(id);
    }

    // 10. 조회수 증가
    @Override
    public int increaseViewCount(Long id) {
        return boardMapper.increaseViewCount(id);
    }

    /* ------------------------------
       관리자 답변
    ------------------------------ */
    // 11. 관리자 답변 등록
    @Override
    public int insertAdminReply(BoardDTO reply) {
        return boardMapper.insertAdminReply(reply);
    }

    /* ------------------------------
       사용자별 조회 (마이페이지)
    ------------------------------ */
    // 12. 사용자별 게시글 총 개수
    @Override
    public int countByUserId(Long userId) {
        return boardMapper.countByUserId(userId);
    }

    // 13. 사용자별 게시글 목록 (페이지네이션)
    @Override
    public List<BoardDTO> findPagedByUserId(Long userId, int offset, int limit) {
        return boardMapper.findPagedByUserId(userId, offset, limit);
    }

    /* ------------------------------
       관리자 기능
    ------------------------------ */
    // 14. 공지사항 목록
    @Override
    public List<BoardDTO> findAllNotices(int offset, int limit) {
        return boardMapper.findAllNotices(offset, limit);
    }

    // 15. 공지사항 총 개수
    @Override
    public int countAllNotices() {
        return boardMapper.countAllNotices();
    }

    // 16. 답글 미등록 요청 게시글 조회
    @Override
    public List<BoardDTO> findUnansweredRequests(int offset, int limit, String filter) {
        return boardMapper.findUnansweredRequests(offset, limit, filter);
    }

    // 17. 답글 미등록 요청 게시글 총 개수
    @Override
    public int countUnansweredRequests(String filter) {
        return boardMapper.countUnansweredRequests(filter);
    }
}