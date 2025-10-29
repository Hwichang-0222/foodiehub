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

    @Override
    public List<BoardDTO> findNoticesByCategory(String category) {
        return boardMapper.findNoticesByCategory(category);
    }

    @Override
    public List<BoardDTO> findNormalPostsByCategory(String category, int page, int size) {
        int offset = (page - 1) * size;
        return boardMapper.findNormalPostsByCategory(category, offset, size);
    }

    @Override
    public int countNormalPostsByCategory(String category) {
        return boardMapper.countNormalPostsByCategory(category);
    }

    @Override
    public BoardDTO findById(Long id) {
        return boardMapper.findById(id);
    }

    @Override
    public List<BoardDTO> findByUserId(Long userId) {
        return boardMapper.findByUserId(userId);
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
    public int insertAdminReply(BoardDTO reply) {
        return boardMapper.insertAdminReply(reply);
    }

    @Override
    public List<BoardDTO> searchBoard(String category, String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return boardMapper.searchBoard(category, keyword, offset, size);
    }

    @Override
    public int countSearchBoards(String category, String keyword) {
        return boardMapper.countSearchBoards(category, keyword);
    }

    @Override
    public int increaseViewCount(Long id) {
        return boardMapper.increaseViewCount(id);
    }

    @Override
    public int countByUserId(Long userId) {
        return boardMapper.countByUserId(userId);
    }

    @Override
    public List<BoardDTO> findPagedByUserId(Long userId, int page, int size) {
        return boardMapper.findPagedByUserId(userId, page, size);
    }

    @Override
    public List<BoardDTO> findAllNotices(int page, int size) {
        int offset = (page - 1) * size;
        return boardMapper.findAllNotices(offset, size);
    }

    @Override
    public int countAllNotices() {
        return boardMapper.countAllNotices();
    }

    @Override
    public List<BoardDTO> findUnansweredRequests(int page, int size, String filter) {
        int offset = (page - 1) * size;
        return boardMapper.findUnansweredRequests(offset, size, filter);
    }

    @Override
    public int countUnansweredRequests(String filter) {
        return boardMapper.countUnansweredRequests(filter);
    }
}
