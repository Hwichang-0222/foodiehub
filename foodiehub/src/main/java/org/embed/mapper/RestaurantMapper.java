package org.embed.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.RestaurantDTO;

@Mapper
public interface RestaurantMapper {

	/* ========================================== */
	/*             기본 CRUD                      */
	/* ========================================== */
	RestaurantDTO findById(@Param("id") Long id);

	int insertRestaurant(RestaurantDTO restaurant);

	int updateRestaurant(RestaurantDTO restaurant);

	int deleteRestaurant(@Param("id") Long id);

	/* ========================================== */
	/*            검색/필터링                     */
	/* ========================================== */
	List<RestaurantDTO> findByFilter(
			@Param("region") String region,
			@Param("category") String category,
			@Param("keyword") String keyword,
			@Param("offset") int offset,
			@Param("limit") int limit
	);

	int countByFilter(
			@Param("region") String region,
			@Param("category") String category,
			@Param("keyword") String keyword
	);

	/* ========================================== */
	/*           관리자 기능                      */
	/* ========================================== */
	List<RestaurantDTO> findAll(Map<String, Object> params);

	int countAllWithOwner(Map<String, Object> params);

	void updateOwner(Map<String, Object> params);

	List<Long> findAssignedOwnerIds();

}
