package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.BoardDTO;

@Mapper
public interface BoardMapper {

    /* ------------------------------
       공지글 조회
    ------------------------------ */
    // 탭별 공지글 (전역공지 + 탭별 공지)
    List<BoardDTO> findNoticesByCategory(@Param("category") String category);

    /* ------------------------------
       일반글 조회
    ------------------------------ */
    // 탭별 일반글 (페이지네이션)
    List<BoardDTO> findNormalPostsByCategory(@Param("category") String category,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    // 일반글 총 개수
    int countNormalPostsByCategory(@Param("category") String category);

    /* ------------------------------
       검색
    ------------------------------ */
    // 게시글 검색 (탭별 카테고리 + 키워드)
    List<BoardDTO> searchBoard(@Param("category") String category,
                               @Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    // 검색 결과 개수
    int countSearchBoards(@Param("category") String category,
                          @Param("keyword") String keyword);

    /* ------------------------------
       기본 CRUD
    ------------------------------ */
    // 단일 게시글 조회
    BoardDTO findById(@Param("id") Long id);

    // 게시글 등록
    int insertBoard(BoardDTO board);

    // 게시글 수정
    int updateBoard(BoardDTO board);

    // 게시글 삭제
    int deleteBoard(@Param("id") Long id);

    // 조회수 증가
    int increaseViewCount(@Param("id") Long id);

    /* ------------------------------
       관리자 답변
    ------------------------------ */
    // 관리자 답변 등록
    int insertAdminReply(BoardDTO reply);

    /* ------------------------------
       사용자별 조회 (마이페이지)
    ------------------------------ */
    // 사용자 작성글 목록
    List<BoardDTO> findByUserId(@Param("userId") Long userId);

    // 사용자별 게시글 총 개수
    int countByUserId(@Param("userId") Long userId);

    // 사용자별 게시글 목록 (페이지네이션)
    List<BoardDTO> findPagedByUserId(@Param("userId") Long userId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    /* ------------------------------
       관리자 기능
    ------------------------------ */
    // 공지사항 목록
    List<BoardDTO> findAllNotices(@Param("offset") int offset,
                                  @Param("limit") int limit);

    // 공지사항 총 개수
    int countAllNotices();

    // 답글 미등록 요청 게시글 조회
    List<BoardDTO> findUnansweredRequests(@Param("offset") int offset,
                                          @Param("limit") int limit,
                                          @Param("filter") String filter);

    // 답글 미등록 요청 게시글 총 개수
    int countUnansweredRequests(@Param("filter") String filter);
}