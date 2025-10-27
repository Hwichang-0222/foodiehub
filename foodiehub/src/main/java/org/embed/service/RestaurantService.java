package org.embed.service;

import java.util.List;
import org.embed.dto.RestaurantDTO;

public interface RestaurantService {

    // 1. 전체 맛집 조회 (페이지네이션)
    List<RestaurantDTO> findAll(int offset, int limit);

    // 2. 단일 맛집 조회
    RestaurantDTO findById(Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(Long id);

    // 6. 조건 검색 (지역 / 카테고리 / 키워드)
    List<RestaurantDTO> findByFilter(String region, String category, String keyword, int offset, int limit);

    // 7. 조건 기반 총 개수 조회
    int countByFilter(String region, String category, String keyword);
}
