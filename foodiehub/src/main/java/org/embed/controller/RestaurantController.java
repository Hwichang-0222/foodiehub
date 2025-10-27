package org.embed.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.embed.dto.ImageDTO;
import org.embed.dto.RestaurantDTO;
import org.embed.dto.ReviewDTO;
import org.embed.service.ImageService;
import org.embed.service.RestaurantService;
import org.embed.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final ImageService imageService;

    // 리스트 보기
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
    
    // 상세보기
    @GetMapping("/detail/{id}")
    public String getRestaurantDetail(@PathVariable("id") Long id, Model model) {
        // 1. 레스토랑 기본 정보 (평균 별점, 리뷰 수 포함)
        RestaurantDTO restaurant = restaurantService.findById(id);

        // 2. 해당 식당의 리뷰 목록
        List<ReviewDTO> reviews = reviewService.findByRestaurantId(id);

        // 3. 리뷰 이미지 목록
        List<ImageDTO> images = imageService.findAllByRestaurantId(id);

        // 4. Model에 담아서 뷰로 전달
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviews", reviews);
        model.addAttribute("images", images);

        // 5. Thymeleaf 템플릿 경로
        return "restaurant/detail";  // => templates/restaurant/detail.html
    }
}
