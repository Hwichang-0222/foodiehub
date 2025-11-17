package org.embed.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<RestaurantDTO> findAll(String keyword, String ownerFilter, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("ownerFilter", ownerFilter);
        params.put("offset", offset);
        params.put("limit", limit);
        return restaurantMapper.findAll(params);
    }
    
    @Override
    public int countAllWithOwner(String keyword, String ownerFilter) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("ownerFilter", ownerFilter);
        return restaurantMapper.countAllWithOwner(params);
    }

    // 3. 오너 지정/변경
    @Override
    public void updateOwner(Long restaurantId, Long ownerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("restaurantId", restaurantId);
        params.put("ownerId", ownerId);
        restaurantMapper.updateOwner(params);
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
    public List<RestaurantDTO> findByFilter(String regionLevel1, String regionLevel2, String category, String keyword, int offset, int limit) {
        return restaurantMapper.findByFilter(regionLevel1, regionLevel2, category, keyword, offset, limit);
    }

    @Override
    public int countByFilter(String regionLevel1, String regionLevel2, String category, String keyword) {
        return restaurantMapper.countByFilter(regionLevel1, regionLevel2, category, keyword);
    }
    
    @Override
    public List<Long> findAssignedOwnerIds() {
        return restaurantMapper.findAssignedOwnerIds();
    }
    
    @Override
    public List<RestaurantDTO> findAllForAI() {
    	// TODO Auto-generated method stub
    	return restaurantMapper.findAllForAI();
    }
    
    @Override
    public RestaurantDTO findByOwnerId(Long ownerId) {
    	// TODO Auto-generated method stub
    	return restaurantMapper.findByOwnerId(ownerId);
    }

}
