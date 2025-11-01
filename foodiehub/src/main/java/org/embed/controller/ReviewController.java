package org.embed.controller;

import java.io.File;
import java.io.IOException;

import org.embed.dto.ImageDTO;
import org.embed.dto.ReviewDTO;
import org.embed.dto.UserDTO;
import org.embed.service.ImageService;
import org.embed.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	
	/* ====================================
	   리뷰 작성 및 댓글 기능
	==================================== */
	
	// 1. 리뷰 작성
	@PostMapping("/write")
    public String writeReview(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            RedirectAttributes redirectAttributes,
            HttpSession session, Model model
    ) throws IOException {
		
		// 세션에서 사용자 정보 조회
		Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/user/login";
        }

		// 사용자 정보 형변환
        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);

		// 새 리뷰 객체 생성
        ReviewDTO review = new ReviewDTO();
		// 식당 ID 설정
        review.setRestaurantId(restaurantId);
		// 사용자 ID 설정
        review.setUserId(user.getId());
		// 별점 설정
        review.setRating(rating);
		// 리뷰 내용 설정
        review.setContent(content);

		// 리뷰 DB에 저장
        reviewService.insertReview(review);

		// 이미지 여러 장 저장
        if (files != null && files.length > 0) {
        	// 업로드 경로 설정
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
					// 리뷰 ID 설정
                    image.setReviewId(review.getId());
					// 파일명 설정
                    image.setFileName(fileName);
					// 파일 경로 설정
                    image.setFileUrl("/uploads/review/" + fileName);
					// 파일 타입 설정
                    image.setFileType(file.getContentType());

					// 이미지 DB에 저장
                    imageService.insertImage(image);
                }
            }
        }

		// 성공 메시지 설정
        redirectAttributes.addFlashAttribute("message", "리뷰와 이미지가 등록되었습니다!");
		// 식당 상세페이지로 리다이렉트
        return "redirect:/restaurant/detail/" + restaurantId;
    }

	// 2. 댓글 작성
	@PostMapping("/reply")
	public String writeReply(
			@RequestParam("parentId") Long parentId,
			@RequestParam("content") String content,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		// 세션에서 사용자 정보 조회
		Object userObj = session.getAttribute("user");
		if (userObj == null) {
			// 사용자가 로그인하지 않았을 경우 로그 기록
			System.out.println("댓글 작성: 세션 사용자 정보 없음");
			return "redirect:/user/login";
		}

		// 사용자 정보 형변환
		UserDTO user = (UserDTO) userObj;
		System.out.println("댓글 작성자: " + user.getId() + ", 이름: " + user.getName());
		
		// 부모 리뷰 정보 조회
		ReviewDTO parentReview = reviewService.findReviewWithUser(parentId);
		System.out.println("부모 리뷰 조회 결과: " + parentReview);
		
		// 부모 리뷰가 없으면 에러 처리
		if (parentReview == null) {
			System.out.println("부모 리뷰를 찾을 수 없음. parentId: " + parentId);
			redirectAttributes.addFlashAttribute("error", "리뷰를 찾을 수 없습니다.");
			return "redirect:/";
		}

		System.out.println("부모 리뷰 restaurantId: " + parentReview.getRestaurantId());

		// 새 댓글 객체 생성
		ReviewDTO reply = new ReviewDTO();
		// 식당 ID 설정
		reply.setRestaurantId(parentReview.getRestaurantId());
		// 사용자 ID 설정
		reply.setUserId(user.getId());
		// 부모 리뷰 ID 설정
		reply.setParentId(parentId);
		// 댓글 내용 설정
		reply.setContent(content);
		// 별점 0으로 설정 (댓글에는 별점 없음)
		reply.setRating(0);

		System.out.println("댓글 저장 시작: " + reply);

		// 댓글 DB에 저장
		int result = reviewService.insertReply(reply);
		System.out.println("댓글 저장 결과: " + result);
		
		// 댓글 저장 성공 여부에 따른 메시지 처리
		if (result > 0) {
			// 성공 메시지 설정
			redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다!");
		} else {
			// 저장 실패 로그 기록
			System.out.println("댓글 저장 실패");
			redirectAttributes.addFlashAttribute("error", "댓글 저장에 실패했습니다.");
		}

		// 식당 상세페이지로 리다이렉트
		return "redirect:/restaurant/detail/" + parentReview.getRestaurantId();
	}

}