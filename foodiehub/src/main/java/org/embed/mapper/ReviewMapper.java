package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.ReviewDTO;

@Mapper
public interface ReviewMapper {

	/* ========================================== */
	/*            기본 CRUD                       */
	/* ========================================== */
	ReviewDTO findReviewWithUser(@Param("id") Long id);

	int insertReview(ReviewDTO review);

	int updateReview(ReviewDTO review);

	int deleteReview(@Param("id") Long id);

	/* ========================================== */
	/*           댓글/대댓글                      */
	/* ========================================== */
	List<ReviewDTO> findRepliesByParentId(@Param("parentId") Long parentId);

	int insertReply(ReviewDTO reply);

	/* ========================================== */
	/*           페이징/조회                      */
	/* ========================================== */
	List<ReviewDTO> findByRestaurantId(@Param("restaurantId") Long restaurantId);

	List<ReviewDTO> findTop5Reviews(@Param("restaurantId") Long restaurantId);

	List<ReviewDTO> findPagedReviews(@Param("restaurantId") Long restaurantId,
									 @Param("offset") int offset,
									 @Param("limit") int limit);

	List<ReviewDTO> findByUserId(@Param("userId") Long userId);

	List<ReviewDTO> findPagedByUserId(@Param("userId") Long userId,
									  @Param("offset") int offset,
									  @Param("limit") int limit);

	/* ========================================== */
	/*              통계                          */
	/* ========================================== */
	Double findAverageRating(@Param("restaurantId") Long restaurantId);

	int countByUserId(@Param("userId") Long userId);

}
