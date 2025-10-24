package org.embed.service;

import org.embed.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {

    // 1. 맛집별 리뷰 목록
    List<ReviewDTO> findByRestaurantId(Long restaurantId);

    // 2. 리뷰 단건 조회
    ReviewDTO findById(Long id);

    // 3. 리뷰 등록
    int insertReview(ReviewDTO review);

    // 4. 댓글/대댓글 등록
    int insertReply(ReviewDTO review);

    // 5. 리뷰 수정
    int updateReview(ReviewDTO review);

    // 6. 리뷰 삭제
    int deleteReview(Long id);

    // 7. 특정 리뷰의 댓글 목록
    List<ReviewDTO> findRepliesByParentId(Long parentId);

    // 8. 최근 리뷰 5개
    List<ReviewDTO> findTop5Reviews(Long restaurantId);

    // 9. 페이징 리뷰
    List<ReviewDTO> findPagedReviews(Long restaurantId, int offset);
}
