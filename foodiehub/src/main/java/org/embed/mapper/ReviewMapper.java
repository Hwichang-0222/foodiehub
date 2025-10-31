package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.ReviewDTO;

@Mapper
public interface ReviewMapper {

    /* ------------------------------
       기본 CRUD
    ------------------------------ */
    // 단일 리뷰 조회 (본문 + 작성자 정보)
    ReviewDTO findReviewWithUser(@Param("id") Long id);

    // 리뷰 등록
    int insertReview(ReviewDTO review);

    // 리뷰 수정
    int updateReview(ReviewDTO review);

    // 리뷰 삭제
    int deleteReview(@Param("id") Long id);

    /* ------------------------------
       댓글/대댓글
    ------------------------------ */
    // 댓글/대댓글 조회
    List<ReviewDTO> findRepliesByParentId(@Param("parentId") Long parentId);

    // 댓글/대댓글 등록
    int insertReply(ReviewDTO reply);

    /* ------------------------------
       페이징/조회
    ------------------------------ */
    // 맛집별 리뷰 목록 (댓글 제외)
    List<ReviewDTO> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 특정 맛집의 최신 리뷰 5개
    List<ReviewDTO> findTop5Reviews(@Param("restaurantId") Long restaurantId);

    // 페이징 처리용 (리뷰 목록)
    List<ReviewDTO> findPagedReviews(@Param("restaurantId") Long restaurantId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    // 사용자별 리뷰 조회
    List<ReviewDTO> findByUserId(@Param("userId") Long userId);

    // 사용자별 리뷰 조회 (페이지네이션)
    List<ReviewDTO> findPagedByUserId(@Param("userId") Long userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    /* ------------------------------
       통계
    ------------------------------ */
    // 맛집별 평균 별점
    Double findAverageRating(@Param("restaurantId") Long restaurantId);

    // 사용자별 리뷰 총 개수
    int countByUserId(@Param("userId") Long userId);

}