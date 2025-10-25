package org.embed.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.embed.dto.BoardDTO;
import org.embed.mapper.BoardMapper;
import org.embed.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public Map<String, Object> getBoardPage(String category, int page, int limit) {
        int offset = (page - 1) * limit;
        List<BoardDTO> notices = boardMapper.findNoticesByCategory(category);
        List<BoardDTO> posts = boardMapper.findNormalPostsByCategory(category, offset, limit);
        int total = boardMapper.countNormalPostsByCategory(category);

        Map<String, Object> result = new HashMap<>();
        result.put("notices", notices);
        result.put("posts", posts);
        result.put("total", total);
        return result;
    }

    @Override
    public BoardDTO getBoardDetail(Long id) {
        boardMapper.increaseViewCount(id);
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
    public List<BoardDTO> findByUserId(Long userId) {
        return boardMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> searchBoards(String category, String keyword, int page, int limit) {
        int offset = (page - 1) * limit;
        List<BoardDTO> results = boardMapper.searchBoard(category, keyword, offset, limit);
        int total = boardMapper.countSearchBoards(category, keyword);

        Map<String, Object> map = new HashMap<>();
        map.put("list", results);
        map.put("total", total);
        return map;
    }

    @Override
    public int insertAdminReply(BoardDTO reply) {
        return boardMapper.insertAdminReply(reply);
    }
}
