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

	/* ==============================
	   공지글 조회
	============================== */
	@Override
	public List<BoardDTO> findNoticesByCategory(String category) {
		return boardMapper.findNoticesByCategory(category);
	}

	/* ==============================
	   일반글 조회
	============================== */
	@Override
	public List<BoardDTO> findNormalPostsByCategory(String category, int offset, int limit) {
		return boardMapper.findNormalPostsByCategory(category, offset, limit);
	}

	@Override
	public int countNormalPostsByCategory(String category) {
		return boardMapper.countNormalPostsByCategory(category);
	}

	/* ==============================
	   검색
	============================== */
	@Override
	public List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit) {
		return boardMapper.searchBoard(category, keyword, offset, limit);
	}

	@Override
	public int countSearchBoards(String category, String keyword) {
		return boardMapper.countSearchBoards(category, keyword);
	}

	/* ==============================
	   기본 CRUD
	============================== */
	@Override
	public BoardDTO findById(Long id) {
		return boardMapper.findById(id);
	}

	@Override
	public int insertBoard(BoardDTO board) {
		return boardMapper.insertBoard(board);
	}

	@Override
	public int updateBoard(BoardDTO board) {
		return boardMapper.updateBoard(board);
	}

	@Override
	public int deleteBoard(Long id) {
		return boardMapper.deleteBoard(id);
	}

	@Override
	public int increaseViewCount(Long id) {
		return boardMapper.increaseViewCount(id);
	}

	/* ==============================
	   관리자 답변
	============================== */
	@Override
	public int insertAdminReply(BoardDTO reply) {
		return boardMapper.insertAdminReply(reply);
	}

	/* ==============================
	   사용자별 조회 (마이페이지)
	============================== */
	@Override
	public int countByUserId(Long userId) {
		return boardMapper.countByUserId(userId);
	}

	@Override
	public List<BoardDTO> findPagedByUserId(Long userId, int offset, int limit) {
		return boardMapper.findPagedByUserId(userId, offset, limit);
	}

	/* ==============================
	   관리자 기능
	============================== */
	@Override
	public List<BoardDTO> findAllNotices(int offset, int limit) {
		return boardMapper.findAllNotices(offset, limit);
	}

	@Override
	public int countAllNotices() {
		return boardMapper.countAllNotices();
	}

	@Override
	public List<BoardDTO> findUnansweredRequests(int offset, int limit, String filter) {
		return boardMapper.findUnansweredRequests(offset, limit, filter);
	}

	@Override
	public int countUnansweredRequests(String filter) {
		return boardMapper.countUnansweredRequests(filter);
	}
}
