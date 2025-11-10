package org.embed.service;

import java.util.List;
import org.embed.dto.RestaurantDTO;

public interface RestaurantService {

	List<RestaurantDTO> findAll(String keyword, String ownerFilter, int offset, int limit);

	RestaurantDTO findById(Long id);

	int insertRestaurant(RestaurantDTO restaurant);

	int updateRestaurant(RestaurantDTO restaurant);

	int deleteRestaurant(Long id);

	List<RestaurantDTO> findByFilter(String region, String category, String keyword, int offset, int limit);

	int countByFilter(String region, String category, String keyword);

	int countAllWithOwner(String keyword, String ownerFilter);

	void updateOwner(Long restaurantId, Long ownerId);

	List<Long> findAssignedOwnerIds();

}
