package org.embed.controller;

import java.security.Principal;
import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.dto.UserDTO;
import org.embed.service.UserService;
import org.embed.service.impl.RecommendServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendServiceImpl recommendService;
    private final UserService userService;

    // 1. 추천 선택 화면 (기분/누구랑/어떤 음식)
    @GetMapping("/menu")
    public String recommendPage() {
        return "recommend/menu-recommend";
    }

    // 2. 추천 처리
    @PostMapping("/menu")
    public String processRecommend(@RequestParam("craving") String craving,
                                   @RequestParam("mood") String mood,
                                   @RequestParam("withWho") String withWho,
                                   Principal principal,
                                   Model model) {

        // 로그인 사용자 정보 가져오기
        UserDTO user = userService.findByEmail(principal.getName());

        // 추천 AI 호출
        List<RestaurantDTO> result =
                recommendService.recommend(user, craving, mood, withWho);

        // 결과 뷰로 전달
        model.addAttribute("restaurants", result);

        return "recommend/menu-recommend-result";
    }
}
