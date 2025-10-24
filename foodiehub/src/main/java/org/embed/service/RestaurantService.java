package org.embed.service;

import java.util.List;
import org.embed.dto.RestaurantDTO;

public interface RestaurantService {

    // 1. 전체 맛집 조회
    List<RestaurantDTO> findAll();

    // 2. 단일 조회 (ID 기준)
    RestaurantDTO findById(Long id);

    // 3. 맛집 등록
    int insertRestaurant(RestaurantDTO restaurant);

    // 4. 맛집 수정
    int updateRestaurant(RestaurantDTO restaurant);

    // 5. 맛집 삭제
    int deleteRestaurant(Long id);

    // 6. 지역 / 카테고리 기반 검색
    List<RestaurantDTO> findByFilter(String region, String category);

    // 7. 이름(키워드) 검색
    List<RestaurantDTO> findByName(String keyword);
}
