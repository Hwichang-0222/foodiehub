package org.embed.service.impl;

import org.embed.dto.ImageDTO;
import org.embed.mapper.ImageMapper;
import org.embed.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public List<ImageDTO> findByReviewId(Long reviewId) {
        return imageMapper.findByReviewId(reviewId);
    }

    @Override
    public List<ImageDTO> findAllByRestaurantId(Long restaurantId) {
        return imageMapper.findAllByRestaurantId(restaurantId);
    }

    @Override
    public List<ImageDTO> findRecent10ByRestaurantId(Long restaurantId) {
        return imageMapper.findRecent10ByRestaurantId(restaurantId);
    }

    @Override
    public int insertImage(ImageDTO image) {
        return imageMapper.insertImage(image);
    }

    @Override
    public int updateImage(ImageDTO image) {
        return imageMapper.updateImage(image);
    }

    @Override
    public int deleteImageById(Long id) {
        return imageMapper.deleteImageById(id);
    }

    @Override
    public int deleteImagesByReviewId(Long reviewId) {
        return imageMapper.deleteImagesByReviewId(reviewId);
    }
}
