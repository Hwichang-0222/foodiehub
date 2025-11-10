package org.embed.service;

import java.util.List;
import org.embed.dto.BoardDTO;

public interface BoardService {

	/* ========================================== */
	/*           공지글 조회                      */
	/* ========================================== */
	List<BoardDTO> findNoticesByCategory(String category);

	/* ========================================== */
	/*           일반글 조회                      */
	/* ========================================== */
	List<BoardDTO> findNormalPostsByCategory(String category, int offset, int limit);

	int countNormalPostsByCategory(String category);

	/* ========================================== */
	/*              검색                          */
	/* ========================================== */
	List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit);

	int countSearchBoards(String category, String keyword);

	/* ========================================== */
	/*            기본 CRUD                       */
	/* ========================================== */
	BoardDTO findById(Long id);

	int insertBoard(BoardDTO board);

	int updateBoard(BoardDTO board);

	int deleteBoard(Long id);

	int increaseViewCount(Long id);

	/* ========================================== */
	/*           관리자 답변                      */
	/* ========================================== */
	int insertAdminReply(BoardDTO reply);

	/* ========================================== */
	/*     사용자별 조회 (마이페이지)             */
	/* ========================================== */
	int countByUserId(Long userId);

	List<BoardDTO> findPagedByUserId(Long userId, int offset, int limit);

	/* ========================================== */
	/*           관리자 기능                      */
	/* ========================================== */
	List<BoardDTO> findAllNotices(int offset, int limit);

	int countAllNotices();

	List<BoardDTO> findUnansweredRequests(int offset, int limit, String filter);

	int countUnansweredRequests(String filter);
}
