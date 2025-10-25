package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.RestaurantDTO;

@Mapper
public interface RestaurantMapper {

    // 1. 전체 맛집 조회 (페이지네이션)
    List<RestaurantDTO> findAll(@Param("offset") int offset, @Param("limit") int limit);

    // 2. 개별 맛집 조회
    RestaurantDTO findById(@Param("id") Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(@Param("id") Long id);

    // 6. 지역 / 카테고리 기반 검색 (페이지네이션)
    List<RestaurantDTO> findByFilter(
            @Param("region") String region,
            @Param("category") String category,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 7. 이름 검색 (페이지네이션)
    List<RestaurantDTO> findByName(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 8. 총 개수 조회
    int countRestaurants();
}
