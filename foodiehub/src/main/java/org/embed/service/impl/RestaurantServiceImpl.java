package org.embed.service.impl;

import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.mapper.RestaurantMapper;
import org.embed.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Override
    public List<RestaurantDTO> findAll(int offset, int limit) {
        return restaurantMapper.findAll(offset, limit);
    }

    @Override
    public RestaurantDTO findById(Long id) {
        return restaurantMapper.findById(id);
    }

    @Override
    public int insertRestaurant(RestaurantDTO restaurant) {
        return restaurantMapper.insertRestaurant(restaurant);
    }

    @Override
    public int updateRestaurant(RestaurantDTO restaurant) {
        return restaurantMapper.updateRestaurant(restaurant);
    }

    @Override
    public int deleteRestaurant(Long id) {
        return restaurantMapper.deleteRestaurant(id);
    }

    @Override
    public List<RestaurantDTO> findByFilter(String region, String category, String keyword, int offset, int limit) {
        return restaurantMapper.findByFilter(region, category, keyword, offset, limit);
    }

    @Override
    public int countByFilter(String region, String category, String keyword) {
        return restaurantMapper.countByFilter(region, category, keyword);
    }
}
