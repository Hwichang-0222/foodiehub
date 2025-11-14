package org.embed.service.impl;

import java.util.List;

import org.embed.dto.MenuDTO;
import org.embed.mapper.MenuMapper;
import org.embed.service.MenuService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public void insertMenu(MenuDTO menu) {
        menuMapper.insertMenu(menu);
    }

    @Override
    public List<MenuDTO> findByRestaurantId(Long restaurantId) {
        return menuMapper.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuDTO findById(Long id) {
        return menuMapper.findById(id);
    }

    @Override
    public void updateMenu(MenuDTO menu) {
        menuMapper.updateMenu(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        menuMapper.deleteMenu(id);
    }

    @Override
    public void deleteByRestaurantId(Long restaurantId) {
        menuMapper.deleteByRestaurantId(restaurantId);
    }
}
