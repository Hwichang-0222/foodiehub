package org.embed.service.impl;

import java.util.List;

import org.embed.dto.ReviewDTO;
import org.embed.mapper.ReviewMapper;
import org.embed.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> findByRestaurantId(Long restaurantId) {
        return reviewMapper.findByRestaurantId(restaurantId);
    }

    @Override
    public ReviewDTO findById(Long id) {
        return reviewMapper.findById(id);
    }

    @Override
    public int insertReview(ReviewDTO review) {
        return reviewMapper.insertReview(review);
    }

    @Override
    public int insertReply(ReviewDTO review) {
        return reviewMapper.insertReply(review);
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
    public List<ReviewDTO> findRepliesByParentId(Long parentId) {
        return reviewMapper.findRepliesByParentId(parentId);
    }

    @Override
    public List<ReviewDTO> findTop5Reviews(Long restaurantId) {
        return reviewMapper.findTop5Reviews(restaurantId);
    }

    @Override
    public List<ReviewDTO> findPagedReviews(Long restaurantId, int offset) {
        return reviewMapper.findPagedReviews(restaurantId, offset);
    }
}
