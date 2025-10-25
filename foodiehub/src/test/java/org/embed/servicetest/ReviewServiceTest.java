package org.embed.servicetest;

import java.util.List;

import org.embed.dto.ReviewDTO;
import org.embed.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

    private static final Long TEST_RESTAURANT_ID = 1L;
    private static Long testReviewId;

    @BeforeAll
    static void setup() {
        System.out.println("\n=== ReviewService 테스트 시작 ===\n");
    }

    // 1. 리뷰 등록
    @Test
    @Order(1)
    void testInsertReview() {
        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(TEST_RESTAURANT_ID);
        review.setUserId(1L);
        review.setContent("1. 서비스 리뷰 등록 테스트");
        review.setRating(5);

        int result = reviewService.insertReview(review);
        testReviewId = review.getId();

        System.out.println("1. 등록된 리뷰 ID: " + testReviewId);
        Assertions.assertEquals(1, result);
    }

    // 2. 댓글 등록
    @Test
    @Order(2)
    void testInsertReply() {
        ReviewDTO reply = new ReviewDTO();
        reply.setRestaurantId(TEST_RESTAURANT_ID);
        reply.setUserId(1L);
        reply.setParentId(testReviewId);
        reply.setContent("2. 서비스 댓글 등록 테스트");

        int result = reviewService.insertReply(reply);
        System.out.println("2. 댓글 등록 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 3. 맛집별 리뷰 목록 조회
    @Test
    @Order(3)
    void testFindByRestaurantId() {
        List<ReviewDTO> list = reviewService.findByRestaurantId(TEST_RESTAURANT_ID);
        System.out.println("3. 리뷰 개수: " + list.size());
        Assertions.assertNotNull(list);
    }

    // 4. 단일 리뷰 조회
    @Test
    @Order(4)
    void testFindReviewWithUser() {
        ReviewDTO dto = reviewService.findReviewWithUser(testReviewId);
        System.out.println("4. 단일 리뷰 내용: " + dto.getContent());
        Assertions.assertNotNull(dto);
    }

    // 5. 댓글 목록 조회
    @Test
    @Order(5)
    void testFindRepliesByParentId() {
        List<ReviewDTO> replies = reviewService.findRepliesByParentId(testReviewId);
        System.out.println("5. 댓글 개수: " + replies.size());
        Assertions.assertFalse(replies.isEmpty());
    }

    // 6. 리뷰 수정
    @Test
    @Order(6)
    void testUpdateReview() {
        ReviewDTO update = new ReviewDTO();
        update.setId(testReviewId);
        update.setContent("6. 수정된 서비스 리뷰 내용");
        update.setRating(4);
        int result = reviewService.updateReview(update);
        System.out.println("6. 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 7. 최신 리뷰 5개 조회
    @Test
    @Order(7)
    void testFindTop5Reviews() {
        List<ReviewDTO> top5 = reviewService.findTop5Reviews(TEST_RESTAURANT_ID);
        System.out.println("7. 최신 리뷰 5개 개수: " + top5.size());
        Assertions.assertNotNull(top5);
    }

    // 8. 페이징 리뷰 목록
    @Test
    @Order(8)
    void testFindPagedReviews() {
        List<ReviewDTO> paged = reviewService.findPagedReviews(TEST_RESTAURANT_ID, 0, 2);
        System.out.println("8. 페이징 리뷰 개수: " + paged.size());
        Assertions.assertNotNull(paged);
    }

    // 9. 평균 별점 계산
    @Test
    @Order(9)
    void testFindAverageRating() {
        Double avg = reviewService.findAverageRating(TEST_RESTAURANT_ID);
        System.out.println("9. 평균 별점: " + avg);
        Assertions.assertNotNull(avg);
    }

    // 10. 리뷰 삭제
    @Test
    @Order(10)
    void testDeleteReview() {
        int result = reviewService.deleteReview(testReviewId);
        System.out.println("10. 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}
