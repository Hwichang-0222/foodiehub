package org.embed.service.impl;

import lombok.RequiredArgsConstructor;
import org.embed.dto.RestaurantDTO;
import org.embed.mapper.RestaurantMapper;
import org.embed.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantMapper restaurantMapper;

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
    public List<RestaurantDTO> findByFilter(String region, String category, int offset, int limit) {
        return restaurantMapper.findByFilter(region, category, offset, limit);
    }

    @Override
    public List<RestaurantDTO> findByName(String keyword, int offset, int limit) {
        return restaurantMapper.findByName(keyword, offset, limit);
    }

    @Override
    public int countRestaurants() {
        return restaurantMapper.countRestaurants();
    }
}
