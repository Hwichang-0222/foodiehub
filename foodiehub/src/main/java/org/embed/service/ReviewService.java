package org.embed.service;

import org.embed.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {

	List<ReviewDTO> findByRestaurantId(Long restaurantId);

	ReviewDTO findReviewWithUser(Long id);

	List<ReviewDTO> findRepliesByParentId(Long parentId);

	int insertReview(ReviewDTO review);

	int insertReply(ReviewDTO reply);

	int updateReview(ReviewDTO review);

	int deleteReview(Long id);

	List<ReviewDTO> findTop5Reviews(Long restaurantId);

	List<ReviewDTO> findPagedReviews(Long restaurantId, int offset, int limit);

	Double findAverageRating(Long restaurantId);

	List<ReviewDTO> findByUserId(Long userId);

	int countByUserId(Long userId);
	List<ReviewDTO> findPagedByUserId(Long userId, int offset, int limit);

}
