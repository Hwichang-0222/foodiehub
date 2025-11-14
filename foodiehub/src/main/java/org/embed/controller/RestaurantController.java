package org.embed.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.embed.dto.AiReviewSummaryDTO;
import org.embed.dto.ImageDTO;
import org.embed.dto.MenuDTO;
import org.embed.dto.MenuImageDTO;
import org.embed.dto.RestaurantDTO;
import org.embed.dto.ReviewDTO;
import org.embed.dto.UserDTO;
import org.embed.service.AiReviewSummaryService;
import org.embed.service.ImageService;
import org.embed.service.MenuImageService;
import org.embed.service.MenuService;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

	private final RestaurantService restaurantService;
	private final ReviewService reviewService;
	private final ImageService imageService;
	private final AiReviewSummaryService aiReviewSummaryService;
	private final MenuService menuService;
	private final MenuImageService menuImageService;
	
	@Value("${kakao.map.api.key}")
	private String kakaoMapApiKey;

	/* ============================================
	   헬퍼 메서드
	============================================ */
	
	// 식당 수정 권한 확인 (ADMIN 또는 해당 식당의 OWNER)
	private boolean hasEditPermission(UserDTO user, RestaurantDTO restaurant) {
		if (user == null) return false;
		
		// ADMIN: 모든 식당 수정 가능
		if ("ROLE_ADMIN".equals(user.getRole())) {
			return true;
		}
		
		// OWNER: 본인 식당만 수정 가능
		if ("ROLE_OWNER".equals(user.getRole()) && 
		    restaurant.getOwnerId() != null && 
		    restaurant.getOwnerId().equals(user.getId())) {
			return true;
		}
		
		return false;
	}

	/* ============================================
	   식당 목록 및 상세 조회
	============================================ */

	// 식당 목록 조회
	@GetMapping("/list")
	public String list(
			@RequestParam(name="regionLevel1", required=false) String regionLevel1,
			@RequestParam(name="regionLevel2", required=false) String regionLevel2,
			@RequestParam(name="category", required=false) String category,
			@RequestParam(name="keyword", required=false) String keyword,
			@RequestParam(name="page", defaultValue="1") int page,
			Model model) {

		int limit = 5;
		int offset = (page - 1) * limit;

		List<RestaurantDTO> restaurants = restaurantService.findByFilter(regionLevel1, regionLevel2, category, keyword, offset, limit);
		int totalCount = restaurantService.countByFilter(regionLevel1, regionLevel2, category, keyword);
		int totalPages = (int) Math.ceil((double) totalCount / limit);

		model.addAttribute("restaurants", restaurants);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("category", category);
		model.addAttribute("regionLevel1", regionLevel1);
	    model.addAttribute("regionLevel2", regionLevel2);
		model.addAttribute("keyword", keyword);

		return "restaurant/restaurant-list";
	}
	
	// 식당 상세 조회
	@GetMapping("/detail/{id}")
	public String getRestaurantDetail(
			@PathVariable("id") Long id, 
			HttpSession session, 
			Model model) {
		
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
		
		// AI 리뷰 요약 조회
		AiReviewSummaryDTO aiSummary = aiReviewSummaryService.findByRestaurantId(id);
		
		List<MenuDTO> menus = menuService.findByRestaurantId(id);
	    List<MenuImageDTO> menuImages = menuImageService.findByRestaurantId(id);

	    model.addAttribute("menus", menus);
	    model.addAttribute("menuImages", menuImages);
	    
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviews", reviews);
		model.addAttribute("images", images);
		model.addAttribute("user", user);
		model.addAttribute("aiSummary", aiSummary);
		model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
		
		return "restaurant/restaurant-detail";
	}

	/* ============================================
	   식당 등록 (ADMIN만 - Spring Security에서 체크)
	============================================ */
	
	// 식당 등록 폼 화면
	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("restaurant", new RestaurantDTO());
		model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
		return "restaurant/restaurant-add";
	}
	
	// 식당 등록 처리
	@PostMapping("/add")
	public String addRestaurant(
			@ModelAttribute RestaurantDTO restaurant,
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

	/* ============================================
	   식당 삭제 (ADMIN만 - Spring Security에서 체크)
	============================================ */
	
	// 식당 삭제
	@GetMapping("/delete/{id}")
	public String deleteRestaurant(
			@PathVariable("id") Long id, 
            RedirectAttributes redirectAttributes) {
		
	    try {
	        restaurantService.deleteRestaurant(id);
	        redirectAttributes.addFlashAttribute("successMessage", "식당이 삭제되었습니다.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "식당 삭제 중 오류가 발생했습니다.");
	    }
	    return "redirect:/admin/dashboard?tab=restaurant";
	}

	/* ============================================
	   식당 수정 (ADMIN 또는 OWNER - 추가 권한 체크)
	============================================ */
	
	// 식당 수정 폼 화면
	@GetMapping("/edit/{id}")
	public String showEditForm(
			@PathVariable("id") Long id, 
			HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
		
	    model.addAttribute("kakaoMapApiKey", kakaoMapApiKey);
	    
	    // 세션에서 사용자 정보 조회
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    RestaurantDTO restaurant = restaurantService.findById(id);
	    
	    // 권한 체크 (ADMIN 또는 해당 식당의 OWNER만)
	    if (!hasEditPermission(user, restaurant)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "수정 권한이 없습니다.");
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    model.addAttribute("restaurant", restaurant);
	    return "restaurant/restaurant-edit";
	}

	// 식당 수정 처리
	@PostMapping("/edit/{id}")
	public String editRestaurant(
			@PathVariable("id") Long id,
            @ModelAttribute RestaurantDTO restaurant,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws IOException {
	    
	    // 세션에서 사용자 정보 조회
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    RestaurantDTO existingRestaurant = restaurantService.findById(id);
	    
	    // 권한 체크 (ADMIN 또는 해당 식당의 OWNER만)
	    if (!hasEditPermission(user, existingRestaurant)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "수정 권한이 없습니다.");
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
	
	/* ============================================
	   AI 리뷰 요약 생성 (ADMIN 또는 OWNER)
	============================================ */

	@PostMapping("/generate-ai-summary/{id}")
	public String generateAiSummary(
	        @PathVariable("id") Long id,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    
	    log.info("===== AI 요약 생성 요청: restaurantId={} =====", id);
	    
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    RestaurantDTO restaurant = restaurantService.findById(id);
	    
	    // 권한 체크 (ADMIN 또는 해당 식당의 OWNER)
	    if (!hasEditPermission(user, restaurant)) {
	        log.warn("권한 없음: user={}", user);
	        redirectAttributes.addFlashAttribute("errorMessage", "권한이 없습니다.");
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
	    try {
	        log.info("AI 요약 생성 서비스 호출");
	        AiReviewSummaryDTO summary = aiReviewSummaryService.generateAndSaveSummary(id);
	        
	        if (summary != null) {
	            redirectAttributes.addFlashAttribute("successMessage", "AI 리뷰 요약이 생성되었습니다!");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "리뷰가 없어 요약을 생성할 수 없습니다.");
	        }
	    } catch (Exception e) {
	        log.error("AI 요약 생성 실패", e);
	        redirectAttributes.addFlashAttribute("errorMessage", "AI 요약 생성에 실패했습니다: " + e.getMessage());
	    }
	    
	    return "redirect:/restaurant/detail/" + id;
	}
}