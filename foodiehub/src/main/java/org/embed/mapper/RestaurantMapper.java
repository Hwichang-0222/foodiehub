package org.embed.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.RestaurantDTO;

@Mapper
public interface RestaurantMapper {

    // 1. 전체 맛집 조회 (페이지네이션)
    List<RestaurantDTO> findAll(Map<String, Object> params);

    // 2. 단일 맛집 조회
    RestaurantDTO findById(@Param("id") Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(@Param("id") Long id);

    // 6. 카테고리 / 지역 / 키워드 기반 검색 (페이지네이션)
    List<RestaurantDTO> findByFilter(
            @Param("region") String region,
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 7. 검색 결과 총 개수
    int countByFilter(
            @Param("region") String region,
            @Param("category") String category,
            @Param("keyword") String keyword
    );
    
    // 8. 총 식당 수
    int countAllWithOwner(Map<String, Object> params);

    // 9. 오너 지정/변경
    void updateOwner(Map<String, Object> params);
    
    List<Long> findAssignedOwnerIds();

}
