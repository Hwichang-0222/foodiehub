package org.embed.mappertest;

import java.util.List;

import org.embed.dto.ImageDTO;
import org.embed.mapper.ImageMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageMapperTest {

    @Autowired
    private ImageMapper imageMapper;

    private static Long testReviewId = 7L;
    private static Long testRestaurantId = 1L;
    private static Long insertedImageId;

    /* ============================================
       이미지 등록
    ============================================ */

    @Test
    @Order(1)
    void testInsertImage() {
        System.out.println("[1] 이미지 등록 테스트");

        ImageDTO image = new ImageDTO();
        image.setReviewId(testReviewId);
        image.setFileName("food_photo_4.jpg");
        image.setFileUrl("/uploads/reviews/food_photo_4.jpg");
        image.setFileType("image/jpeg");

        int result = imageMapper.insertImage(image);
        insertedImageId = image.getId();

        System.out.println("  - 삽입 결과: " + result + ", 생성된 이미지 ID: " + insertedImageId);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       이미지 조회
    ============================================ */

    @Test
    @Order(2)
    void testFindByReviewId() {
        System.out.println("[2] 리뷰별 이미지 조회 테스트");
        List<ImageDTO> images = imageMapper.findByReviewId(testReviewId);

        for (ImageDTO img : images) {
            System.out.printf("  - ID: %d | 파일명: %s | URL: %s%n", img.getId(), img.getFileName(), img.getFileUrl());
        }

        Assertions.assertFalse(images.isEmpty());
    }

    @Test
    @Order(3)
    void testFindAllByRestaurantId() {
        System.out.println("[3] 특정 식당의 전체 이미지 조회 테스트");
        List<ImageDTO> images = imageMapper.findAllByRestaurantId(testRestaurantId);

        for (ImageDTO img : images) {
            System.out.printf("  - 리뷰ID: %d | 파일명: %s | URL: %s%n", img.getReviewId(), img.getFileName(), img.getFileUrl());
        }

        Assertions.assertFalse(images.isEmpty());
    }

    @Test
    @Order(4)
    void testFindRecent10ByRestaurantId() {
        System.out.println("[4] 특정 식당의 최신 이미지 10장 조회 테스트");
        List<ImageDTO> images = imageMapper.findRecent10ByRestaurantId(testRestaurantId);

        for (ImageDTO img : images) {
            System.out.printf("  - 리뷰ID: %d | 파일명: %s | 등록일: %s%n", img.getReviewId(), img.getFileName(), img.getCreatedAt());
        }

        Assertions.assertTrue(images.size() <= 10);
    }

    /* ============================================
       이미지 수정
    ============================================ */

    @Test
    @Order(5)
    void testUpdateImage() {
        System.out.println("[5] 이미지 수정 테스트");

        ImageDTO img = new ImageDTO();
        img.setId(insertedImageId);
        img.setFileName("updated_photo.jpg");
        img.setFileUrl("/uploads/reviews/updated_photo.jpg");
        img.setFileType("image/png");

        int result = imageMapper.updateImage(img);
        System.out.println("  - 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       이미지 삭제
    ============================================ */

    @Test
    @Order(6)
    void testDeleteImageById() {
        System.out.println("[6] 이미지 삭제 테스트");

        int result = imageMapper.deleteImageById(insertedImageId);
        System.out.println("  - 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}