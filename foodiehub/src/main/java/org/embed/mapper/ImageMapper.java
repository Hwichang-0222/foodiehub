package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.ImageDTO;

@Mapper
public interface ImageMapper {

	/* ========================================== */
	/*              조회                          */
	/* ========================================== */
	List<ImageDTO> findByReviewId(@Param("reviewId") Long reviewId);

	List<ImageDTO> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);

	List<ImageDTO> findRecent10ByRestaurantId(@Param("restaurantId") Long restaurantId);

	/* ========================================== */
	/*            등록/수정                       */
	/* ========================================== */
	int insertImage(ImageDTO image);

	int updateImage(ImageDTO image);

	/* ========================================== */
	/*              삭제                          */
	/* ========================================== */
	int deleteImageById(@Param("id") Long id);

	int deleteImagesByReviewId(@Param("reviewId") Long reviewId);
}
