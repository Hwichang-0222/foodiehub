package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.BoardDTO;

@Mapper
public interface BoardMapper {

    // 1. 탭별 공지글 (전역공지 + 해당 카테고리 공지)
    List<BoardDTO> findNoticesByCategory(@Param("category") String category);

    // 2. 탭별 일반글 (페이지네이션)
    List<BoardDTO> findNormalPostsByCategory(
        @Param("category") String category,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 3. 일반글 총 개수 (페이지네이션용)
    int countNormalPostsByCategory(@Param("category") String category);

    // 4. 단일 게시글 조회
    BoardDTO findById(@Param("id") Long id);

    // 5. 사용자 작성글 목록
    List<BoardDTO> findByUserId(@Param("userId") Long userId);

    // 6. 게시글 등록
    int insertBoard(BoardDTO board);

    // 7. 게시글 수정
    int updateBoard(BoardDTO board);

    // 8. 게시글 삭제
    int deleteBoard(@Param("id") Long id);

    // 9. 관리자 답변 등록 (parent_id 기반)
    int insertAdminReply(BoardDTO reply);

    // 10. 검색 (탭별 category + 키워드)
    List<BoardDTO> searchBoard(
        @Param("category") String category,
        @Param("keyword") String keyword,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 11. 검색 결과 개수
    int countSearchBoards(
        @Param("category") String category,
        @Param("keyword") String keyword
    );
    
    // 12. 조회수 증가
    int increaseViewCount(@Param("id") Long id);
}
