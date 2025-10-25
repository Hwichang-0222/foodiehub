package org.embed.mappertest;

import java.util.List;

import org.embed.dto.ReviewDTO;
import org.embed.mapper.ReviewMapper;
import org.junit.jupiter.api.Assertions;
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

    private static Long testReviewId;
    private static Long testReplyId;

    // 1. 리뷰 등록 (최상위)
    @Test
    @Order(1)
    void testInsertReview() {
        System.out.println("[1] 리뷰 등록 테스트");

        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(1L); // 존재하는 맛집 ID로 교체
        review.setUserId(2L);       // 존재하는 유저 ID로 교체
        review.setContent("리뷰 본문입니다. 정말 맛있어요!");
        review.setRating(5);
        review.setIsReply(false);

        int result = reviewMapper.insertReview(review);
        testReviewId = review.getId();

        System.out.println("등록 결과: " + result);
        System.out.println("생성된 리뷰 ID: " + testReviewId);
        Assertions.assertEquals(1, result);
    }

    // 2. 댓글 등록 (부모는 리뷰)
    @Test
    @Order(2)
    void testInsertReply() {
        System.out.println("[2] 댓글 등록 테스트");

        ReviewDTO reply = new ReviewDTO();
        reply.setRestaurantId(1L);
        reply.setUserId(1L);
        reply.setParentId(testReviewId);
        reply.setContent("댓글입니다!");
        reply.setIsReply(true);

        int result = reviewMapper.insertReply(reply);
        testReplyId = reply.getId();

        System.out.println("등록 결과: " + result);
        System.out.println("생성된 댓글 ID: " + testReplyId);
        Assertions.assertEquals(1, result);
    }

    // 3. 대댓글 등록 (부모는 댓글)
    @Test
    @Order(3)
    void testInsertReplyToReply() {
        System.out.println("[3] 대댓글 등록 테스트 (2단계까지만 허용)");

        ReviewDTO replyToReply = new ReviewDTO();
        replyToReply.setRestaurantId(1L);
        replyToReply.setUserId(1L);
        replyToReply.setParentId(testReplyId);
        replyToReply.setContent("대댓글 테스트");
        replyToReply.setIsReply(true);

        int result = reviewMapper.insertReply(replyToReply);

        System.out.println("등록 결과 (2단계 제한): " + result);
        Assertions.assertEquals(1, result);
    }

    // 4. 대대댓글 시도 (차단되어야 함)
    @Test
    @Order(4)
    void testInsertReplyToReplyOfReply() {
        System.out.println("[4] 대대댓글 등록 테스트 (차단되어야 함)");

        // 대댓글의 ID를 가져와 부모로 설정 (3단계)
        ReviewDTO deepReply = new ReviewDTO();
        deepReply.setRestaurantId(1L);
        deepReply.setUserId(1L);
        deepReply.setParentId(testReplyId + 1); // 존재하는 대댓글 ID를 가정
        deepReply.setContent("이건 3단계라 안 들어가야 함");
        deepReply.setIsReply(true);

        int result = reviewMapper.insertReply(deepReply);
        System.out.println("등록 결과: " + result);
        Assertions.assertEquals(0, result, "3단계 댓글은 차단되어야 합니다.");
    }

    // 5. 맛집별 리뷰 조회
    @Test
    @Order(5)
    void testFindByRestaurantId() {
        System.out.println("[5] 맛집별 리뷰 조회 테스트");

        List<ReviewDTO> reviews = reviewMapper.findByRestaurantId(1L);
        reviews.forEach(r ->
                System.out.printf("리뷰 ID: %d, 작성자: %s, 내용: %s%n",
                        r.getId(), r.getUserName(), r.getContent())
        );
        Assertions.assertFalse(reviews.isEmpty());
    }

    // 6. 댓글 조회
    @Test
    @Order(6)
    void testFindRepliesByParentId() {
        System.out.println("[6] 댓글 조회 테스트");

        List<ReviewDTO> replies = reviewMapper.findRepliesByParentId(testReviewId);
        replies.forEach(r ->
                System.out.printf("댓글 ID: %d, 내용: %s%n", r.getId(), r.getContent())
        );
        Assertions.assertFalse(replies.isEmpty());
    }

    // 7. 최신 리뷰 5개 (뷰용)
    @Test
    @Order(7)
    void testFindTop5Reviews() {
        System.out.println("[7] 최신 리뷰 5개 조회 테스트");
        List<ReviewDTO> topReviews = reviewMapper.findTop5Reviews(1L);
        topReviews.forEach(r ->
                System.out.printf("리뷰 ID: %d, 작성자: %s, 별점: %d%n",
                        r.getId(), r.getUserName(), r.getRating())
        );
    }

    // 8. 리뷰 수정
    @Test
    @Order(8)
    void testUpdateReview() {
        System.out.println("[8] 리뷰 수정 테스트");

        ReviewDTO review = new ReviewDTO();
        review.setId(testReviewId);
        review.setContent("수정된 리뷰 내용입니다.");
        review.setRating(4);

        int result = reviewMapper.updateReview(review);
        System.out.println("수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 9. 리뷰 삭제
    @Test
    @Order(9)
    void testDeleteReview() {
        System.out.println("[9] 리뷰 삭제 테스트");

        int result = reviewMapper.deleteReview(testReviewId);
        System.out.println("삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}
