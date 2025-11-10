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

	@PostMapping("/write")
	public String writeReview(
			@RequestParam("restaurantId") Long restaurantId,
			@RequestParam("rating") int rating,
			@RequestParam("content") String content,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			HttpSession session,
			RedirectAttributes redirectAttributes
	) throws IOException {

		UserDTO user = (UserDTO) session.getAttribute("user");

		ReviewDTO review = new ReviewDTO();
		review.setRestaurantId(restaurantId);
		review.setUserId(user.getId());
		review.setRating(rating);
		review.setContent(content);

		reviewService.insertReview(review);

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
					image.setReviewId(review.getId());
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

	@PostMapping("/reply")
	public String writeReply(
			@RequestParam("parentId") Long parentId,
			@RequestParam("content") String content,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		UserDTO user = (UserDTO) session.getAttribute("user");
		ReviewDTO parentReview = reviewService.findReviewWithUser(parentId);

		if (parentReview == null) {
			redirectAttributes.addFlashAttribute("error", "리뷰를 찾을 수 없습니다.");
			return "redirect:/";
		}

		ReviewDTO reply = new ReviewDTO();
		reply.setRestaurantId(parentReview.getRestaurantId());
		reply.setUserId(user.getId());
		reply.setParentId(parentId);
		reply.setContent(content);
		reply.setRating(0);

		int result = reviewService.insertReply(reply);

		if (result > 0) {
			redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다!");
		} else {
			redirectAttributes.addFlashAttribute("error", "댓글 저장에 실패했습니다.");
		}

		return "redirect:/restaurant/detail/" + parentReview.getRestaurantId();
	}

}
