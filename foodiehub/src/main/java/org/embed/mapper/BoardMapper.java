package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.BoardDTO;

@Mapper
public interface BoardMapper {

	/* ========================================== */
	/*           공지글 조회                      */
	/* ========================================== */
	List<BoardDTO> findNoticesByCategory(@Param("category") String category);

	/* ========================================== */
	/*           일반글 조회                      */
	/* ========================================== */
	List<BoardDTO> findNormalPostsByCategory(@Param("category") String category,
											 @Param("offset") int offset,
											 @Param("limit") int limit);

	int countNormalPostsByCategory(@Param("category") String category);

	/* ========================================== */
	/*              검색                          */
	/* ========================================== */
	List<BoardDTO> searchBoard(@Param("category") String category,
							   @Param("keyword") String keyword,
							   @Param("offset") int offset,
							   @Param("limit") int limit);

	int countSearchBoards(@Param("category") String category,
						  @Param("keyword") String keyword);

	/* ========================================== */
	/*            기본 CRUD                       */
	/* ========================================== */
	BoardDTO findById(@Param("id") Long id);

	int insertBoard(BoardDTO board);

	int updateBoard(BoardDTO board);

	int deleteBoard(@Param("id") Long id);

	int increaseViewCount(@Param("id") Long id);

	/* ========================================== */
	/*           관리자 답변                      */
	/* ========================================== */
	int insertAdminReply(BoardDTO reply);

	/* ========================================== */
	/*     사용자별 조회 (마이페이지)             */
	/* ========================================== */
	List<BoardDTO> findByUserId(@Param("userId") Long userId);

	int countByUserId(@Param("userId") Long userId);

	List<BoardDTO> findPagedByUserId(@Param("userId") Long userId,
									 @Param("offset") int offset,
									 @Param("limit") int limit);

	/* ========================================== */
	/*           관리자 기능                      */
	/* ========================================== */
	List<BoardDTO> findAllNotices(@Param("offset") int offset,
								  @Param("limit") int limit);

	int countAllNotices();

	List<BoardDTO> findUnansweredRequests(@Param("offset") int offset,
										  @Param("limit") int limit,
										  @Param("filter") String filter);

	int countUnansweredRequests(@Param("filter") String filter);
}
