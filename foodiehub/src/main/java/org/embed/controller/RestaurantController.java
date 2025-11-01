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
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${kakao.map.api.key}")
	private String kakaoMapApiKey;

	// 1. 식당 목록 조회
	@GetMapping("/list")
	public String list(
			@RequestParam(name="region", required=false) String region,
			@RequestParam(name="category", required=false) String category,
			@RequestParam(name="keyword", required=false) String keyword,
			@RequestParam(name="page", defaultValue="1") int page,
			Model model) {

		int limit = 5;
		int offset = (page - 1) * limit;

		List<RestaurantDTO> restaurants = restaurantService.findByFilter(region, category, keyword, offset, limit);
		int totalCount = restaurantService.countByFilter(region, category, keyword);
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		model.addAttribute("restaurants", restaurants);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("category", category);
		model.addAttribute("region", region);
		model.addAttribute("keyword", keyword);

		return "restaurant/restaurant-list";
	}
	
	// 2. 식당 상세 조회
	@GetMapping("/detail/{id}")
	public String getRestaurantDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
		RestaurantDTO restaurant = restaurantService.findById(id);
		List<ReviewDTO> reviews = reviewService.findByRestaurantId(id);
		
		for (ReviewDTO review : reviews) {
			List<ReviewDTO> replies = reviewService.findRepliesByParentId(review.getId());
			review.setReplies(replies);
		}
		
		for (ReviewDTO review : reviews) {
			List<ImageDTO> reviewImages = imageService.findByReviewId(review.getId());
			review.setImages(reviewImages);
		}

		List<ImageDTO> images = imageService.findAllByRestaurantId(id);
		UserDTO user = (UserDTO) session.getAttribute("user");

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviews", reviews);
		model.addAttribute("images", images);
		model.addAttribute("user", user);
		model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);

		return "restaurant/restaurant-detail";
	}
	
	// 3. 식당 등록 폼 화면
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("restaurant", new RestaurantDTO());
		model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
		return "restaurant/restaurant-add";
	}
	
	// 4. 식당 등록 처리
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
	
	// 5. 식당 삭제
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
	
	// 6. 식당 수정 폼 화면
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, 
	                          HttpSession session, 
	                          Model model) {
	    model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
	    RestaurantDTO restaurant = restaurantService.findById(id);
	    boolean hasPermission = false;
	    
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             restaurant.getOwnerId() != null && 
	             restaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    model.addAttribute("restaurant", restaurant);
	    return "restaurant/restaurant-edit";
	}

	// 7. 식당 수정 처리
	@PostMapping("/edit/{id}")
	public String editRestaurant(@PathVariable("id") Long id,
	                            @ModelAttribute RestaurantDTO restaurant,
	                            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
	                            HttpSession session,
	                            RedirectAttributes redirectAttributes) throws IOException {
	    
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
	    RestaurantDTO existingRestaurant = restaurantService.findById(id);
	    boolean hasPermission = false;
	    
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             existingRestaurant.getOwnerId() != null && 
	             existingRestaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    if (mainImage != null && !mainImage.isEmpty()) {
	        String uploadDir = System.getProperty("user.dir") + "/uploads/restaurant/";
	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();
	        
	        String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
	        mainImage.transferTo(new File(dir, fileName));
	        restaurant.setMainImageUrl("/uploads/restaurant/" + fileName);
	    } else {
	        restaurant.setMainImageUrl(existingRestaurant.getMainImageUrl());
	    }
	    
	    restaurant.setId(id);
	    restaurant.setOwnerId(existingRestaurant.getOwnerId());
	    restaurantService.updateRestaurant(restaurant);
	    
	    redirectAttributes.addFlashAttribute("successMessage", "식당 정보가 수정되었습니다.");
	    return "redirect:/restaurant/detail/" + id;
	}
}