package org.embed.mappertest;

import java.util.List;

import org.embed.dto.ReviewDTO;
import org.embed.mapper.ReviewMapper;
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
class ReviewMapperTest {

    @Autowired
    private ReviewMapper reviewMapper;

    private static final Long TEST_RESTAURANT_ID = 3L;
    private static Long testReviewId;

    @BeforeAll
    static void setup() {
        System.out.println("\n=== ReviewMapper 테스트 시작 ===\n");
    }

    // 1. 리뷰 등록 테스트
    @Test
    @Order(1)
    void testInsertReview() {
        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(TEST_RESTAURANT_ID);
        review.setUserId(1L);
        review.setContent("1. 매퍼 리뷰 등록 테스트");
        review.setRating(4);

        int result = reviewMapper.insertReview(review);
        testReviewId = review.getId();

        System.out.println("1. 등록된 리뷰 ID: " + testReviewId);
        Assertions.assertEquals(1, result);
    }

    // 2. 댓글 등록 테스트
    @Test
    @Order(2)
    void testInsertReply() {
        ReviewDTO reply = new ReviewDTO();
        reply.setRestaurantId(TEST_RESTAURANT_ID);
        reply.setUserId(1L);
        reply.setParentId(testReviewId);
        reply.setContent("2. 매퍼 댓글 등록 테스트");

        int result = reviewMapper.insertReply(reply);
        System.out.println("2. 댓글 등록 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 3. 리뷰 목록 조회
    @Test
    @Order(3)
    void testFindByRestaurantId() {
        List<ReviewDTO> list = reviewMapper.findByRestaurantId(TEST_RESTAURANT_ID);
        System.out.println("3. 리뷰 목록 개수: " + list.size());
        Assertions.assertNotNull(list);
    }

    // 4. 단일 리뷰 조회
    @Test
    @Order(4)
    void testFindReviewWithUser() {
        ReviewDTO dto = reviewMapper.findReviewWithUser(testReviewId);
        System.out.println("4. 단일 리뷰 내용: " + dto.getContent());
        Assertions.assertNotNull(dto);
    }

    // 5. 댓글 조회
    @Test
    @Order(5)
    void testFindRepliesByParentId() {
        List<ReviewDTO> replies = reviewMapper.findRepliesByParentId(testReviewId);
        System.out.println("5. 댓글 개수: " + replies.size());
        Assertions.assertFalse(replies.isEmpty());
    }

    // 6. 리뷰 수정
    @Test
    @Order(6)
    void testUpdateReview() {
        ReviewDTO update = new ReviewDTO();
        update.setId(testReviewId);
        update.setContent("6. 수정된 리뷰 내용");
        update.setRating(5);
        int result = reviewMapper.updateReview(update);
        System.out.println("6. 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 7. 최신 리뷰 5개
    @Test
    @Order(7)
    void testFindTop5Reviews() {
        List<ReviewDTO> top5 = reviewMapper.findTop5Reviews(TEST_RESTAURANT_ID);
        System.out.println("7. 최신 리뷰 5개 개수: " + top5.size());
        Assertions.assertNotNull(top5);
    }

    // 8. 페이징 처리 리뷰 목록
    @Test
    @Order(8)
    void testFindPagedReviews() {
        List<ReviewDTO> paged = reviewMapper.findPagedReviews(TEST_RESTAURANT_ID, 0, 3);
        System.out.println("8. 페이징 리뷰 개수: " + paged.size());
        Assertions.assertNotNull(paged);
    }

    // 9. 평균 별점 계산
    @Test
    @Order(9)
    void testFindAverageRating() {
        Double avg = reviewMapper.findAverageRating(TEST_RESTAURANT_ID);
        System.out.println("9. 평균 별점: " + avg);
        Assertions.assertNotNull(avg);
    }

    // 10. 리뷰 삭제
    @Test
    @Order(10)
    void testDeleteReview() {
        int result = reviewMapper.deleteReview(testReviewId);
        System.out.println("10. 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}
