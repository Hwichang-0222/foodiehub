package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.dto.ReviewDTO;
import org.embed.service.ReviewService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    private static Long testReviewId;
    private static Long testRestaurantId = 1L; // 테스트용 (DB에 존재하는 맛집 ID 사용)
    private static Long testUserId = 1L;       // 테스트용 (DB에 존재하는 유저 ID 사용)

    @Test
    @Order(1)
    void testInsertReview() {
        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(testRestaurantId);
        review.setUserId(testUserId);
        review.setContent("서비스 테스트용 리뷰입니다.");
        review.setRating(5);
        review.setIsReply(false);

        int result = reviewService.insertReview(review);
        assertEquals(1, result);
        assertNotNull(review.getId());
        testReviewId = review.getId();

        System.out.println("1. insertReview ID=" + testReviewId);
    }

    @Test
    @Order(2)
    void testFindById() {
        ReviewDTO review = reviewService.findById(testReviewId);
        assertNotNull(review);
        assertEquals("서비스 테스트용 리뷰입니다.", review.getContent());
        System.out.println("2. findById: " + review.getContent());
    }

    @Test
    @Order(3)
    void testInsertReply() {
        ReviewDTO reply = new ReviewDTO();
        reply.setRestaurantId(testRestaurantId);
        reply.setUserId(testUserId);
        reply.setParentId(testReviewId);
        reply.setContent("댓글 테스트입니다.");
        reply.setIsReply(true);

        int result = reviewService.insertReply(reply);
        assertEquals(1, result);
        System.out.println("3. insertReply: " + reply.getContent());
    }

    @Test
    @Order(4)
    void testFindRepliesByParentId() {
        List<ReviewDTO> replies = reviewService.findRepliesByParentId(testReviewId);
        assertTrue(replies.size() > 0);
        System.out.println("4. findRepliesByParentId count: " + replies.size());
    }

    @Test
    @Order(5)
    void testUpdateReview() {
        ReviewDTO review = reviewService.findById(testReviewId);
        review.setContent("수정된 리뷰 내용");
        int result = reviewService.updateReview(review);
        assertEquals(1, result);

        ReviewDTO updated = reviewService.findById(testReviewId);
        assertEquals("수정된 리뷰 내용", updated.getContent());
        System.out.println("5. updateReview: " + updated.getContent());
    }

    @Test
    @Order(6)
    void testFindByRestaurantId() {
        List<ReviewDTO> reviews = reviewService.findByRestaurantId(testRestaurantId);
        assertTrue(reviews.size() > 0);
        System.out.println("6. findByRestaurantId count: " + reviews.size());
    }

    @Test
    @Order(7)
    void testFindTop5Reviews() {
        List<ReviewDTO> top5 = reviewService.findTop5Reviews(testRestaurantId);
        assertNotNull(top5);
        System.out.println("7. findTop5Reviews count: " + top5.size());
    }

    @Test
    @Order(8)
    void testFindPagedReviews() {
        List<ReviewDTO> page = reviewService.findPagedReviews(testRestaurantId, 0);
        assertNotNull(page);
        System.out.println("8. findPagedReviews count: " + page.size());
    }

    @Test
    @Order(9)
    void testDeleteReview() {
        int result = reviewService.deleteReview(testReviewId);
        assertEquals(1, result);
        System.out.println("9. deleteReview 완료");
    }
}
