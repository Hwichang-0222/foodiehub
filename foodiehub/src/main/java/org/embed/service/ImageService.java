package org.embed.service;

import org.embed.dto.ImageDTO;
import java.util.List;

public interface ImageService {

	List<ImageDTO> findByReviewId(Long reviewId);

	List<ImageDTO> findAllByRestaurantId(Long restaurantId);

	List<ImageDTO> findRecent10ByRestaurantId(Long restaurantId);

	int insertImage(ImageDTO image);

	int updateImage(ImageDTO image);

	int deleteImageById(Long id);

	int deleteImagesByReviewId(Long reviewId);
}
