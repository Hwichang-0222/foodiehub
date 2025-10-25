package org.embed.service;

import org.embed.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {

    // 1. 맛집별 리뷰 목록 (댓글 제외)
    List<ReviewDTO> findByRestaurantId(Long restaurantId);

    // 2. 단일 리뷰 조회 (본문 + 작성자 정보)
    ReviewDTO findReviewWithUser(Long id);

    // 3. 댓글/대댓글 조회
    List<ReviewDTO> findRepliesByParentId(Long parentId);

    // 4. 리뷰 등록
    int insertReview(ReviewDTO review);

    // 5. 댓글/대댓글 등록
    int insertReply(ReviewDTO reply);

    // 6. 리뷰 수정
    int updateReview(ReviewDTO review);

    // 7. 리뷰 삭제
    int deleteReview(Long id);

    // 8. 특정 맛집의 최신 리뷰 5개
    List<ReviewDTO> findTop5Reviews(Long restaurantId);

    // 9. 페이징 처리용 (리뷰 목록)
    List<ReviewDTO> findPagedReviews(Long restaurantId, int offset, int limit);

    // 10. 맛집별 평균 별점
    Double findAverageRating(Long restaurantId);
}
