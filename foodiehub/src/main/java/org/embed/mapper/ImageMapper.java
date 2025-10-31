package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.ImageDTO;

@Mapper
public interface ImageMapper {

    /* ------------------------------
       조회
    ------------------------------ */
    // 리뷰 ID별 이미지 조회
    List<ImageDTO> findByReviewId(@Param("reviewId") Long reviewId);

    // 특정 식당의 전체 리뷰 이미지 조회 (사진 탭)
    List<ImageDTO> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 특정 식당의 최신 이미지 10장 조회 (썸네일용)
    List<ImageDTO> findRecent10ByRestaurantId(@Param("restaurantId") Long restaurantId);

    /* ------------------------------
       등록/수정
    ------------------------------ */
    // 이미지 등록
    int insertImage(ImageDTO image);
    
    // 이미지 교체
    int updateImage(ImageDTO image);

    /* ------------------------------
       삭제
    ------------------------------ */
    // 단일 삭제
    int deleteImageById(@Param("id") Long id);
    
    // 리뷰 단위 삭제 (교체용)
    int deleteImagesByReviewId(@Param("reviewId") Long reviewId);
}