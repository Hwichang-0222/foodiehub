package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.dto.ImageDTO;
import org.embed.service.ImageService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    private static Long testImageId;
    private static Long testReviewId = 7L;       // DB에 존재하는 리뷰 ID 사용
    private static Long testRestaurantId = 1L;   // DB에 존재하는 식당 ID 사용

    @Test
    @Order(1)
    void testInsertImage() {
        ImageDTO image = new ImageDTO();
        image.setReviewId(testReviewId);
        image.setFileName("test_image.jpg");
        image.setFileUrl("https://example.com/test_image.jpg");
        image.setFileType("image/jpeg");

        int result = imageService.insertImage(image);
        assertEquals(1, result);
        assertNotNull(image.getId());
        testImageId = image.getId();

        System.out.println("1. insertImage: id=" + testImageId);
    }

    @Test
    @Order(2)
    void testFindByReviewId() {
        List<ImageDTO> images = imageService.findByReviewId(testReviewId);
        assertNotNull(images);
        assertTrue(images.size() > 0);
        System.out.println("2. findByReviewId count=" + images.size());
    }

    @Test
    @Order(3)
    void testFindAllByRestaurantId() {
        List<ImageDTO> images = imageService.findAllByRestaurantId(testRestaurantId);
        assertNotNull(images);
        System.out.println("3. findAllByRestaurantId count=" + images.size());
    }

    @Test
    @Order(4)
    void testFindRecent10ByRestaurantId() {
        List<ImageDTO> images = imageService.findRecent10ByRestaurantId(testRestaurantId);
        assertNotNull(images);
        System.out.println("4. findRecent10ByRestaurantId count=" + images.size());
    }

    @Test
    @Order(5)
    void testUpdateImage() {
        ImageDTO image = new ImageDTO();
        image.setId(testImageId);
        image.setReviewId(testReviewId);
        image.setFileName("updated_image.jpg");
        image.setFileUrl("https://example.com/updated_image.jpg");
        image.setFileType("image/png");

        int result = imageService.updateImage(image);
        assertEquals(1, result);
        System.out.println("5. updateImage 완료");
    }

    @Test
    @Order(6)
    void testDeleteImageById() {
        int result = imageService.deleteImageById(testImageId);
        assertEquals(1, result);
        System.out.println("6. deleteImageById 완료 (id=" + testImageId + ")");
    }

    @Test
    @Order(7)
    void testDeleteImagesByReviewId() {
        int result = imageService.deleteImagesByReviewId(testReviewId);
        System.out.println("7. deleteImagesByReviewId (reviewId=" + testReviewId + ", 삭제 수=" + result + ")");
    }
}
