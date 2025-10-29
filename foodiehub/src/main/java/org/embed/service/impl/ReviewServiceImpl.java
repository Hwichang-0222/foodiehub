package org.embed.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.embed.dto.ReviewDTO;
import org.embed.mapper.ReviewMapper;
import org.embed.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> findByRestaurantId(Long restaurantId) {
        return reviewMapper.findByRestaurantId(restaurantId);
    }

    @Override
    public ReviewDTO findReviewWithUser(Long id) {
        return reviewMapper.findReviewWithUser(id);
    }

    @Override
    public List<ReviewDTO> findRepliesByParentId(Long parentId) {
        return reviewMapper.findRepliesByParentId(parentId);
    }

    @Override
    public int insertReview(ReviewDTO review) {
        return reviewMapper.insertReview(review);
    }

    @Override
    public int insertReply(ReviewDTO reply) {
        return reviewMapper.insertReply(reply);
    }

    @Override
    public int updateReview(ReviewDTO review) {
        return reviewMapper.updateReview(review);
    }

    @Override
    public int deleteReview(Long id) {
        return reviewMapper.deleteReview(id);
    }

    @Override
    public List<ReviewDTO> findTop5Reviews(Long restaurantId) {
        return reviewMapper.findTop5Reviews(restaurantId);
    }

    @Override
    public List<ReviewDTO> findPagedReviews(Long restaurantId, int offset, int limit) {
        return reviewMapper.findPagedReviews(restaurantId, offset, limit);
    }

    @Override
    public Double findAverageRating(Long restaurantId) {
        return reviewMapper.findAverageRating(restaurantId);
    }

    // 추가된 메서드
    @Override
    public List<ReviewDTO> findByUserId(Long userId) {
        return reviewMapper.findByUserId(userId);
    }

    @Override
    public int countByUserId(Long userId) {
        return reviewMapper.countByUserId(userId);
    }

    @Override
    public List<ReviewDTO> findPagedByUserId(Long userId, int offset, int limit) {
        return reviewMapper.findPagedByUserId(userId, offset, limit);
    }
}
