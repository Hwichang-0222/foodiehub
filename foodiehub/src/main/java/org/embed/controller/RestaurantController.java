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

	// 1. 식당 목록 조회
    @GetMapping("/list")
    public String list(
    		@RequestParam(name="region", required=false) String region,
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="keyword", required=false) String keyword,
            @RequestParam(name="page", defaultValue="1") int page,
            Model model) {

        int limit = 5;
		// 페이지에 따른 오프셋 계산
        int offset = (page - 1) * limit;

		// 필터 조건에 따른 식당 목록 조회
        List<RestaurantDTO> restaurants = restaurantService.findByFilter(region, category, keyword, offset, limit);

		// 필터 조건에 따른 총 개수 조회
        int totalCount = restaurantService.countByFilter(region, category, keyword);
		// 이 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / limit);

		// 모델에 리스트 및 페이징 정보 담기
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category", category);
        model.addAttribute("region", region);
        model.addAttribute("keyword", keyword);

        return "restaurant/list";
    }
    
    // 2. 식당 상세 조회
    @GetMapping("/detail/{id}")
    public String getRestaurantDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
		// 식당 기본정보 조회
        RestaurantDTO restaurant = restaurantService.findById(id);

		// 해당 식당의 리뷰 목록 조회
        List<ReviewDTO> reviews = reviewService.findByRestaurantId(id);
		
		// 각 리뷰의 댓글 조회 - 추가됨!
		for (ReviewDTO review : reviews) {
			// 각 리뷰별 댓글 목록 조회
			List<ReviewDTO> replies = reviewService.findRepliesByParentId(review.getId());
			// 리뷰 객체에 댓글 설정
			review.setReplies(replies);
			System.out.println("리뷰 ID: " + review.getId() + ", 댓글 개수: " + (replies != null ? replies.size() : 0));
		}
		
		// 각 리뷰의 이미지 조회 - 추가됨!
		for (ReviewDTO review : reviews) {
			// 각 리뷰별 이미지 조회
			List<ImageDTO> reviewImages = imageService.findByReviewId(review.getId());
			// 리뷰 객체에 이미지 설정
			review.setImages(reviewImages);
			System.out.println("리뷰 ID: " + review.getId() + ", 이미지 개수: " + (reviewImages != null ? reviewImages.size() : 0));
		}

		// 리뷰 이미지 목록 조회
        List<ImageDTO> images = imageService.findAllByRestaurantId(id);

		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		// 댓글 기능 디버깅: 세션에 등록된 사용자 정보 확인
		System.out.println("=== [RestaurantController.detail] User Session Info ===");
		System.out.println("Session object: " + session);
		System.out.println("User from session: " + user);
		if (user != null) {
			System.out.println("Logged-in User ID: " + user.getId());
			System.out.println("Logged-in User Name: " + user.getName());
			System.out.println("Logged-in User Email: " + user.getEmail());
		} else {
			System.out.println("User is NULL - 사용자가 로그인하지 않았음");
		}

		// 모델에 데이터 담기
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviews", reviews);
        model.addAttribute("images", images);
		// 모델에 사용자 정보 담기
		model.addAttribute("user", user);

        return "restaurant/detail";
    }
    
    // 3. 식당 등록 폼 화면
	@GetMapping("/add")
	public String showAddForm(Model model) {
		// 새로운 식당 객체 생성
		model.addAttribute("restaurant", new RestaurantDTO());
		return "restaurant/add";
	}
	
	// 4. 식당 등록 처리
	@PostMapping("/add")
	public String addRestaurant(@ModelAttribute RestaurantDTO restaurant,
	                            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
	                            RedirectAttributes redirectAttributes) throws IOException {

		// 이미지 파일 업로드 처리
	    if (mainImage != null && !mainImage.isEmpty()) {
	        String uploadDir = System.getProperty("user.dir") + "/uploads/restaurant/";
	        File dir = new File(uploadDir);
			// 디렉토리 없으면 생성
	        if (!dir.exists()) dir.mkdirs();
	        
			// 파일명 생성 및 저장
	        String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
	        mainImage.transferTo(new File(dir, fileName));
	        
			// 식당에 이미지 경로 설정
	        restaurant.setMainImageUrl("/uploads/restaurant/" + fileName);
	    }
	    
		// 식당 정보 DB에 등록
	    restaurantService.insertRestaurant(restaurant);
		// 성공 메시지 전달
	    redirectAttributes.addFlashAttribute("successMessage", "식당이 성공적으로 등록되었습니다.");
	    return "redirect:/admin/dashboard?tab=restaurant";
	}
	
	// 5. 식당 삭제
	@GetMapping("/delete/{id}")
	public String deleteRestaurant(@PathVariable("id") Long id, 
	                               RedirectAttributes redirectAttributes) {
	    try {
			// 식당 삭제 처리
	        restaurantService.deleteRestaurant(id);
			// 성공 메시지 전달
	        redirectAttributes.addFlashAttribute("successMessage", "식당이 삭제되었습니다.");
	    } catch (Exception e) {
			// 오류 메시지 전달
	        redirectAttributes.addFlashAttribute("errorMessage", "식당 삭제 중 오류가 발생했습니다.");
	    }
	    return "redirect:/admin/dashboard?tab=restaurant";
	}
	
	// 6. 식당 수정 폼 화면
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, 
	                          HttpSession session, 
	                          Model model) {
		// 세션에서 사용자 정보 조회
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
		// 로그인 여부 확인
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
		// 식당 정보 조회
	    RestaurantDTO restaurant = restaurantService.findById(id);
	    
		// 권한 확인
	    boolean hasPermission = false;
	    
		// 관리자이면 수정 가능
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
		// 식당 소유자이고 ID가 일치하면 수정 가능
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             restaurant.getOwnerId() != null && 
	             restaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
		// 권한 없으면 상세페이지로 리다이렉트
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
		// 모델에 식당 정보 담기
	    model.addAttribute("restaurant", restaurant);
	    return "restaurant/edit";
	}

	// 7. 식당 수정 처리
	@PostMapping("/edit/{id}")
	public String editRestaurant(@PathVariable("id") Long id,
	                            @ModelAttribute RestaurantDTO restaurant,
	                            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
	                            HttpSession session,
	                            RedirectAttributes redirectAttributes) throws IOException {
	    
		// 세션에서 사용자 정보 조회
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    
		// 로그인 여부 확인
	    if (user == null) {
	        return "redirect:/user/login";
	    }
	    
		// 기존 식당 정보 조회
	    RestaurantDTO existingRestaurant = restaurantService.findById(id);
	    
		// 권한 확인
	    boolean hasPermission = false;
	    
		// 관리자이면 수정 가능
	    if (user.getRole().equals("ROLE_ADMIN")) {
	        hasPermission = true;
	    }
		// 식당 소유자이고 ID가 일치하면 수정 가능
	    else if (user.getRole().equals("ROLE_OWNER") && 
	             existingRestaurant.getOwnerId() != null && 
	             existingRestaurant.getOwnerId().equals(user.getId())) {
	        hasPermission = true;
	    }
	    
		// 권한 없으면 상세페이지로 리다이렉트
	    if (!hasPermission) {
	        return "redirect:/restaurant/detail/" + id;
	    }
	    
		// 이미지 파일 업로드 처리
	    if (mainImage != null && !mainImage.isEmpty()) {
	        String uploadDir = System.getProperty("user.dir") + "/uploads/restaurant/";
	        File dir = new File(uploadDir);
			// 디렉토리 없으면 생성
	        if (!dir.exists()) dir.mkdirs();
	        
			// 파일명 생성 및 저장
	        String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
	        mainImage.transferTo(new File(dir, fileName));
	        
			// 식당에 새 이미지 경로 설정
	        restaurant.setMainImageUrl("/uploads/restaurant/" + fileName);
	    } else {
			// 기존 이미지 유지
	        restaurant.setMainImageUrl(existingRestaurant.getMainImageUrl());
	    }
	    
		// 식당 ID 설정
	    restaurant.setId(id);
		// 소유자 정보 유지
	    restaurant.setOwnerId(existingRestaurant.getOwnerId());
		// DB에 식당 정보 업데이트
	    restaurantService.updateRestaurant(restaurant);
	    
		// 성공 메시지 전달
	    redirectAttributes.addFlashAttribute("successMessage", "식당 정보가 수정되었습니다.");
	    return "redirect:/restaurant/detail/" + id;
	}
}