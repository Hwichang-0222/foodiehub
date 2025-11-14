package org.embed.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.MenuImageDTO;

@Mapper
public interface MenuImageMapper {

    // 메뉴 이미지 등록
    int insertMenuImage(MenuImageDTO image);

    // 특정 식당의 메뉴 이미지 전체 조회
    List<MenuImageDTO> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 단일 이미지 조회
    MenuImageDTO findById(@Param("id") Long id);

    // 이미지 삭제
    int deleteMenuImage(@Param("id") Long id);

    // 식당 삭제 시 이미지 전체 삭제
    int deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}
