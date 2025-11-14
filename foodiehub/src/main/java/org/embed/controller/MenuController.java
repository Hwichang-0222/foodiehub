package org.embed.controller;

import java.io.File;

import org.embed.dto.MenuDTO;
import org.embed.dto.MenuImageDTO;
import org.embed.service.MenuImageService;
import org.embed.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuImageService menuImageService;

    @GetMapping("/manage")
    public String manageMenus(@RequestParam(name = "restaurantId") Long restaurantId, Model model) {

        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("menus", menuService.findByRestaurantId(restaurantId));
        model.addAttribute("menuImages", menuImageService.findByRestaurantId(restaurantId));

        return "menu/menu-manage";
    }
    
    @PostMapping("/image/add")
    @ResponseBody
    public MenuImageDTO uploadMenuImage(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 실제 저장 경로
            String uploadPath = System.getProperty("user.dir") + "/uploads/menu/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // 파일명
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 파일 저장
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);

            // DTO 생성
            MenuImageDTO dto = new MenuImageDTO();
            dto.setRestaurantId(restaurantId);
            dto.setImageUrl("/uploads/menu/" + fileName);

            // DB 저장
            menuImageService.insertMenuImage(dto);

            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @PostMapping("/image/delete/{id}")
    @ResponseBody
    public boolean deleteMenuImage(@PathVariable(name = "id") Long id) {

        // DB에서 먼저 불러옴
        MenuImageDTO dto = menuImageService.findById(id);

        if (dto != null) {
            // 실제 파일 삭제
            String path = System.getProperty("user.dir") + dto.getImageUrl();
            File file = new File(path);
            if (file.exists()) file.delete();
        }

        // DB 삭제
        menuImageService.deleteMenuImage(id);

        return true;
    }


    @PostMapping("/add")
    @ResponseBody
    public MenuDTO addMenu(@ModelAttribute MenuDTO menu) {
        menuService.insertMenu(menu);
        return menu; // JSON 반환 → JS가 테이블에 추가
    }

    @PostMapping("/update")
    @ResponseBody
    public MenuDTO updateMenu(@ModelAttribute MenuDTO menu) {
        menuService.updateMenu(menu);
        return menu;
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return "OK";
    }

}