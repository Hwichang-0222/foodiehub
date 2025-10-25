package org.embed.service;

import org.embed.dto.RestaurantDTO;
import java.util.List;

public interface RestaurantService {

    // 1. 전체 맛집 조회 (페이지네이션)
    List<RestaurantDTO> findAll(int offset, int limit);

    // 2. 개별 맛집 조회
    RestaurantDTO findById(Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(Long id);

    // 6. 지역 / 카테고리 기반 검색 (페이지네이션)
    List<RestaurantDTO> findByFilter(String region, String category, int offset, int limit);

    // 7. 이름 검색 (페이지네이션)
    List<RestaurantDTO> findByName(String keyword, int offset, int limit);

    // 8. 총 개수 조회
    int countRestaurants();
}
