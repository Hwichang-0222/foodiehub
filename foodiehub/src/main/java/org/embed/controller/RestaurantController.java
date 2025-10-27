package org.embed.controller;

import lombok.RequiredArgsConstructor;
import org.embed.dto.RestaurantDTO;
import org.embed.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/list")
    public String list(
    		@RequestParam(name="region", required=false) String region,
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="keyword", required=false) String keyword,
            @RequestParam(name="page", defaultValue="1") int page,
            Model model) {

        int limit = 5;
        int offset = (page - 1) * limit;

        // 1. 리스트 데이터
        List<RestaurantDTO> restaurants = restaurantService.findByFilter(region, category, keyword, offset, limit);

        // 2. 총 개수
        int totalCount = restaurantService.countByFilter(region, category, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / limit);

        // 3. 모델에 전달
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category", category);
        model.addAttribute("region", region);
        model.addAttribute("keyword", keyword);

        return "restaurant/list";
    }

}
