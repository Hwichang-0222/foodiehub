package org.embed.service.impl;

import java.util.List;

import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
import org.embed.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<BoardDTO> findAll(int offset, int limit) {
        return boardMapper.findAll(offset, limit);
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
    public List<BoardDTO> searchBoard(String category, String keyword, int offset, int limit) {
        return boardMapper.searchBoard(category, keyword, offset, limit);
    }

    @Override
    public int countBoards(String category, String keyword) {
        return boardMapper.countBoards(category, keyword);
    }
}
