package org.embed.controller;

import java.io.File;
import java.io.IOException;

import org.embed.dto.ImageDTO;
import org.embed.dto.ReviewDTO;
import org.embed.dto.UserDTO;
import org.embed.service.ImageService;
import org.embed.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
	
    private final ReviewService reviewService;
    private final ImageService imageService;
	
	/* ============================================
	   리뷰 작성 및 댓글 기능
	============================================ */
	
	// 리뷰 작성
	@PostMapping("/write")
    public String writeReview(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) throws IOException {
		
		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");

		// 새 리뷰 객체 생성
        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(restaurantId);
        review.setUserId(user.getId());
        review.setRating(rating);
        review.setContent(content);

		// 리뷰 DB에 저장
        reviewService.insertReview(review);

		// 이미지 여러 장 저장
        if (files != null && files.length > 0) {
        	String uploadPath = System.getProperty("user.dir") + "/uploads/review/";

			// 업로드 디렉토리 생성
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

			// 각 이미지 파일 처리
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
					// 파일명 생성
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
					// 파일 저장
                    File dest = new File(uploadDir, fileName);
                    file.transferTo(dest);

					// 이미지 정보 객체 생성
                    ImageDTO image = new ImageDTO();
                    image.setReviewId(review.getId());
                    image.setFileName(fileName);
                    image.setFileUrl("/uploads/review/" + fileName);
                    image.setFileType(file.getContentType());

					// 이미지 DB에 저장
                    imageService.insertImage(image);
                }
            }
        }

		// 성공 메시지 설정
        redirectAttributes.addFlashAttribute("message", "리뷰와 이미지가 등록되었습니다!");
		// 식당 상세페이지로 리다이렉트
        return "redirect:/restaurant/detail/" + restaurantId + "#reviews";
    }

	// 댓글 작성
	@PostMapping("/reply")
	public String writeReply(
			@RequestParam("parentId") Long parentId,
			@RequestParam("content") String content,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		// 세션에서 사용자 정보 조회
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		// 부모 리뷰 정보 조회
		ReviewDTO parentReview = reviewService.findReviewWithUser(parentId);
		
		// 부모 리뷰가 없으면 에러 처리
		if (parentReview == null) {
			redirectAttributes.addFlashAttribute("error", "리뷰를 찾을 수 없습니다.");
			return "redirect:/";
		}

		// 새 댓글 객체 생성
		ReviewDTO reply = new ReviewDTO();
		reply.setRestaurantId(parentReview.getRestaurantId());
		reply.setUserId(user.getId());
		reply.setParentId(parentId);
		reply.setContent(content);
		reply.setRating(0); // 댓글에는 별점 없음

		// 댓글 DB에 저장
		int result = reviewService.insertReply(reply);
		
		// 댓글 저장 성공 여부에 따른 메시지 처리
		if (result > 0) {
			redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다!");
		} else {
			redirectAttributes.addFlashAttribute("error", "댓글 저장에 실패했습니다.");
		}

		// 식당 상세페이지로 리다이렉트
		return "redirect:/restaurant/detail/" + parentReview.getRestaurantId() + "#reviews";
	}
	
	// 리뷰 수정
	@PostMapping("/update")
	public String updateReview(
	        @RequestParam("id") Long id,
	        @RequestParam("rating") int rating,
	        @RequestParam("content") String content,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    ReviewDTO review = reviewService.findReviewWithUser(id);
	    
	    // 권한 체크 (본인만 수정 가능)
	    if (!review.getUserId().equals(user.getId())) {
	        redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
	        return "redirect:/user/mypage";
	    }
	    
	    review.setRating(rating);
	    review.setContent(content);
	    reviewService.updateReview(review);
	    
	    redirectAttributes.addFlashAttribute("message", "리뷰가 수정되었습니다.");
	    return "redirect:/user/mypage";
	}

	// 리뷰 삭제
	@PostMapping("/delete")
	public String deleteReview(
	        @RequestParam("id") Long id,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    
	    UserDTO user = (UserDTO) session.getAttribute("user");
	    ReviewDTO review = reviewService.findReviewWithUser(id);
	    
	    // 권한 체크 (본인만 삭제 가능)
	    if (!review.getUserId().equals(user.getId())) {
	        redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
	        return "redirect:/user/mypage";
	    }
	    
	    reviewService.deleteReview(id);
	    redirectAttributes.addFlashAttribute("message", "리뷰가 삭제되었습니다.");
	    return "redirect:/user/mypage";
	}

}