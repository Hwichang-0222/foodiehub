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
	
	@PostMapping("/write")
    public String writeReview(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            RedirectAttributes redirectAttributes,
            HttpSession session, Model model
    ) throws IOException {
		
		Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/user/login";
        }

        UserDTO user = (UserDTO) userObj;
        model.addAttribute("user", user);

        // 1. 리뷰 등록
        ReviewDTO review = new ReviewDTO();
        review.setRestaurantId(restaurantId);
        review.setUserId(user.getId()); // 로그인 기능 연동 시 교체
        review.setRating(rating);
        review.setContent(content);

        reviewService.insertReview(review); // useGeneratedKeys로 id 채워짐

        // 2. 이미지 여러 장 저장
        if (files != null && files.length > 0) {
        	String uploadPath = System.getProperty("user.dir") + "/uploads/review/";


            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    File dest = new File(uploadDir, fileName);
                    file.transferTo(dest);

                    ImageDTO image = new ImageDTO();
                    image.setReviewId(review.getId()); // FK 연결
                    image.setFileName(fileName);
                    image.setFileUrl("/uploads/review/" + fileName);
                    image.setFileType(file.getContentType());

                    imageService.insertImage(image);
                }
            }
        }

        redirectAttributes.addFlashAttribute("message", "리뷰와 이미지가 등록되었습니다!");
        return "redirect:/restaurant/detail/" + restaurantId;
    }

}
