package org.embed.service;

import org.embed.dto.ImageDTO;
import java.util.List;

public interface ImageService {

    // 1. 리뷰 ID별 이미지 조회
    List<ImageDTO> findByReviewId(Long reviewId);

    // 2. 특정 식당의 전체 리뷰 이미지 조회
    List<ImageDTO> findAllByRestaurantId(Long restaurantId);

    // 3. 특정 식당의 최신 이미지 10장 조회
    List<ImageDTO> findRecent10ByRestaurantId(Long restaurantId);

    // 4. 이미지 등록
    int insertImage(ImageDTO image);

    // 5. 이미지 교체
    int updateImage(ImageDTO image);

    // 6. 단일 삭제
    int deleteImageById(Long id);

    // 7. 리뷰 단위 삭제
    int deleteImagesByReviewId(Long reviewId);
}
