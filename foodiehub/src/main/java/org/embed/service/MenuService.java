package org.embed.service;

import java.util.List;

import org.embed.dto.MenuDTO;

public interface MenuService {

    // 메뉴 등록
    void insertMenu(MenuDTO menu);

    // 식당별 메뉴 목록 조회
    List<MenuDTO> findByRestaurantId(Long restaurantId);

    // 단일 메뉴 조회
    MenuDTO findById(Long id);

    // 메뉴 수정
    void updateMenu(MenuDTO menu);

    // 메뉴 삭제
    void deleteMenu(Long id);

    // 식당 삭제 시 메뉴 전체 삭제
    void deleteByRestaurantId(Long restaurantId);
}
