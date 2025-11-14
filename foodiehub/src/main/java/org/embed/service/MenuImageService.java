package org.embed.service;

import java.util.List;

import org.embed.dto.MenuImageDTO;

public interface MenuImageService {

    // 이미지 등록
    void insertMenuImage(MenuImageDTO image);

    // 식당별 이미지 조회
    List<MenuImageDTO> findByRestaurantId(Long restaurantId);

    // 단일 이미지 조회
    MenuImageDTO findById(Long id);

    // 이미지 삭제
    void deleteMenuImage(Long id);

    // 식당 삭제 시 전체 이미지 삭제
    void deleteByRestaurantId(Long restaurantId);
}
