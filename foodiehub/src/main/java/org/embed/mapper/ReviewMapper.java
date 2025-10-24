package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.ReviewDTO;

@Mapper
public interface ReviewMapper {

    // 1️. 맛집의 전체 리뷰 조회 (최신순, 댓글 제외)
    List<ReviewDTO> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 2️. 리뷰 단건 조회
    ReviewDTO findById(Long id);

    // 3️. 리뷰 등록
    int insertReview(ReviewDTO review);

    // 4️. 댓글 / 대댓글 등록 (2단계까지만 허용)
    int insertReply(ReviewDTO reply);

    // 5️. 리뷰 수정
    int updateReview(ReviewDTO review);

    // 6️. 리뷰 삭제
    int deleteReview(Long id);

    // 7️. 댓글 조회
    List<ReviewDTO> findRepliesByParentId(Long parentId);

    // 8️. 최신 리뷰 5개 (맛집 상세용 축약)
    List<ReviewDTO> findTop5Reviews(Long restaurantId);

    // 9️. 페이지 단위 리뷰 조회 (1페이지 1리뷰 + 댓글 포함)
    List<ReviewDTO> findPagedReviews(@Param("restaurantId") Long restaurantId,
                                     @Param("offset") int offset);
    
}
