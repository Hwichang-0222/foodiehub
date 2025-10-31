package org.embed.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.embed.dto.ImageDTO;
import org.embed.dto.RestaurantDTO;
import org.embed.dto.ReviewDTO;
import org.embed.dto.UserDTO;
import org.embed.service.ImageService;
import org.embed.service.RestaurantService;
import org.embed.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
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
    
    // 식당 등록 폼 이동
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("restaurant", new RestaurantDTO());
		return "restaurant/add"; // templates/restaurant/add.html
	}
	
	// 식당 등록 처리
	@PostMapping("/add")
	public String addRestaurant(@ModelAttribute RestaurantDTO restaurant,
	                            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
	                            RedirectAttributes redirectAttributes) throws IOException {

	    if (mainImage != null && !mainImage.isEmpty()) {
	        String uploadDir = System.getProperty("user.dir") + "/uploads/restaurant/";
	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();
	        
	        String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
	        mainImage.transferTo(new File(dir, fileName));
	        
	        restaurant.setMainImageUrl("/uploads/restaurant/" + fileName);
	    }
	    
	    restaurantService.insertRestaurant(restaurant);
	    redirectAttributes.addFlashAttribute("successMessage", "식당이 성공적으로 등록되었습니다.");
	    return "redirect:/admin/dashboard?tab=restaurant";
	}
	
	// 식당 삭제 (관리자 전용)
	@GetMapping("/delete/{id}")
	public String deleteRestaurant(@PathVariable("id") Long id, 
	                               RedirectAttributes redirectAttributes) {
	    try {
	        restaurantService.deleteRestaurant(id);
	        redirectAttributes.addFlashAttribute("successMessage", "식당이 삭제되었습니다.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "식당 삭제 중 오류가 발생했습니다.");
	    }
	    return "redirect:/admin/dashboard?tab=restaurant";
	}
	
	// 식당 수정 폼 이동
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, 
	                          HttpSession session, 
	                          Model model) {
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
	    // 로그인 확인
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
	    RestaurantDTO restaurant = restaurantService.findById(id);
	    
	    // 권한 확인
	    boolean hasPermission = false;
	    
	    // 1. 관리자면 무조건 가능
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
	    // 2. ROLE_OWNER이면서 restaurant의 ownerId와 user의 id가 일치하는 경우만 가능
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             restaurant.getOwnerId() != null && 
	             restaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    model.addAttribute("restaurant", restaurant);
	    return "restaurant/edit";
	}

	// 식당 수정 처리
	@PostMapping("/edit/{id}")
	public String editRestaurant(@PathVariable("id") Long id,
	                            @ModelAttribute RestaurantDTO restaurant,
	                            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
	                            HttpSession session,
	                            RedirectAttributes redirectAttributes) throws IOException {
	    
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
	    // 로그인 확인
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
	    RestaurantDTO existingRestaurant = restaurantService.findById(id);
	    
	    // 권한 확인
	    boolean hasPermission = false;
	    
	    // 1. 관리자면 무조건 가능
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
	    // 2. ROLE_OWNER이면서 restaurant의 ownerId와 user의 id가 일치하는 경우만 가능
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             existingRestaurant.getOwnerId() != null && 
	             existingRestaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    // 이미지 업로드
	    if (mainImage != null && !mainImage.isEmpty()) {
	        String uploadDir = System.getProperty("user.dir") + "/uploads/restaurant/";
	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();
	        
	        String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
	        mainImage.transferTo(new File(dir, fileName));
	        
	        restaurant.setMainImageUrl("/uploads/restaurant/" + fileName);
	    } else {
	        // 기존 이미지 유지
	        restaurant.setMainImageUrl(existingRestaurant.getMainImageUrl());
	    }
	    
	    restaurant.setId(id);
	    restaurant.setOwnerId(existingRestaurant.getOwnerId()); // 오너 정보 유지
	    restaurantService.updateRestaurant(restaurant);
	    
	    redirectAttributes.addFlashAttribute("successMessage", "식당 정보가 수정되었습니다.");
	    return "redirect:/restaurant/detail/" + id;
	}
}
