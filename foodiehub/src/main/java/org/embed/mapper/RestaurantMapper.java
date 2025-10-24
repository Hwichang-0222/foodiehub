package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.RestaurantDTO;

@Mapper
public interface RestaurantMapper {

	// 1️. 전체 맛집 목록 조회
	List<RestaurantDTO> findAll();

	// 2️. 개별 맛집 조회 (id)
	RestaurantDTO findById(Long id);

	// 3️. 맛집 등록
	int insertRestaurant(RestaurantDTO restaurant);

	// 4️. 맛집 수정
	int updateRestaurant(RestaurantDTO restaurant);

	// 5️. 맛집 삭제
	int deleteRestaurant(Long id);

	// 6️. 지역별 검색
	List<RestaurantDTO> findByRegion(@Param("region") String region);

	// 7️. 카테고리별 검색
	List<RestaurantDTO> findByCategory(@Param("category") String category);
	
	// 8. 이름으로 검색 (부분 검색 포함)
	List<RestaurantDTO> findByName(@Param("keyword") String keyword);
}
