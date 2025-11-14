package org.embed.service;

import java.util.List;
import org.embed.dto.RestaurantDTO;

public interface RestaurantService {

    // 1. 전체 맛집 조회 (페이지네이션)
    List<RestaurantDTO> findAll(String keyword, String ownerFilter, int offset, int limit);

    // 2. 단일 맛집 조회
    RestaurantDTO findById(Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(Long id);

    // 6. 조건 검색 (지역 / 카테고리 / 키워드)
    List<RestaurantDTO> findByFilter(String regionLevel1, String regionLevel2, String category, String keyword, int offset, int limit);

    // 7. 조건 기반 총 개수 조회
    int countByFilter(String regionLevel1, String regionLevel2, String category, String keyword);
    
	// 8. 총 식당 개수 (검색/필터 조건 포함)
    int countAllWithOwner(String keyword, String ownerFilter);

    // 9. 식당 오너 지정/변경
    void updateOwner(Long restaurantId, Long ownerId);
    
    List<Long> findAssignedOwnerIds();

}
