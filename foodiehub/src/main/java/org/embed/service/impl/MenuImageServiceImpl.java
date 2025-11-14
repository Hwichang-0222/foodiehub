package org.embed.service.impl;

import java.util.List;

import org.embed.dto.MenuImageDTO;
import org.embed.mapper.MenuImageMapper;
import org.embed.service.MenuImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuImageServiceImpl implements MenuImageService {

    private final MenuImageMapper menuImageMapper;

    @Override
    public void insertMenuImage(MenuImageDTO image) {
        menuImageMapper.insertMenuImage(image);
    }

    @Override
    @Transactional(readOnly = true)  // 조회는 readOnly
    public List<MenuImageDTO> findByRestaurantId(Long restaurantId) {
        return menuImageMapper.findByRestaurantId(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)  // 조회는 readOnly
    public MenuImageDTO findById(Long id) {
        return menuImageMapper.findById(id);
    }

    @Override
    public void deleteMenuImage(Long id) {
        menuImageMapper.deleteMenuImage(id);
    }

    @Override
    public void deleteByRestaurantId(Long restaurantId) {
        menuImageMapper.deleteByRestaurantId(restaurantId);
    }
}