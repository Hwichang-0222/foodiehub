package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.MenuDTO;

@Mapper
public interface MenuMapper {

    // 메뉴 등록
    int insertMenu(MenuDTO menu);

    // 특정 식당 메뉴 전체 조회
    List<MenuDTO> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 단일 메뉴 조회
    MenuDTO findById(@Param("id") Long id);

    // 메뉴 수정
    int updateMenu(MenuDTO menu);

    // 메뉴 삭제
    int deleteMenu(@Param("id") Long id);

    // 식당 삭제 시 메뉴 전체 삭제
    int deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}
