package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.BoardDTO;

@Mapper
public interface BoardMapper {

    // 1. 전체 게시글 목록 (공지 우선, 최신순)
    List<BoardDTO> findAll(@Param("offset") int offset, @Param("limit") int limit);

    // 2. 단일 게시글 조회 (비공개 게시글 접근 제한은 Service 단에서 제어)
    BoardDTO findById(@Param("id") Long id);

    // 3. 사용자 작성글 목록 (마이페이지용)
    List<BoardDTO> findByUserId(@Param("userId") Long userId);

    // 4. 게시글 작성
    int insertBoard(BoardDTO board);

    // 5. 게시글 수정
    int updateBoard(BoardDTO board);

    // 6. 게시글 삭제
    int deleteBoard(@Param("id") Long id);

    // 7. 관리자 답변 등록 (parent_id 설정)
    int insertAdminReply(BoardDTO reply);

    // 8. 검색 (카테고리, 제목, 작성자)
    List<BoardDTO> searchBoard(@Param("category") String category, 
    		@Param("keyword") String keyword,
    		@Param("offset") int offset,
    		@Param("limit") int limit);

    // 9. 총 게시글 수 (페이지네이션용)
    int countBoards(@Param("category") String category,
                    @Param("keyword") String keyword);
}
