package org.embed.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /* ============================================
       AI 맛집 추천
    ============================================ */

    // 추천 설문 페이지
    @GetMapping("/menu")
    public String recommendPage(Principal principal, Model model) {

        if (principal != null) {
            UserDTO user = userService.findByEmail(principal.getName());
            model.addAttribute("loginUser", user);
        }

        return "recommend/menu-recommend";
    }

    // 추천 처리 (비로그인 사용자도 지원)
    @PostMapping("/menu")
    public String processRecommend(@RequestParam(value = "gender", required = false) String gender,
                                   @RequestParam(value = "ageGroup", required = false) String ageGroup,
                                   @RequestParam(value = "region", required = false) String region,
                                   @RequestParam(value = "district", required = false) String district,
                                   @RequestParam("craving") String craving,
                                   @RequestParam("mood") String mood,
                                   @RequestParam("withWho") String withWho,
                                   Principal principal,
                                   Model model) {

        UserDTO user;

        // 로그인한 사용자인 경우
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        } 
        // 비로그인 사용자인 경우 - 임시 UserDTO 생성
        else {
            user = new UserDTO();
            user.setGender(gender);
            
            // 연령대 → 나이 → birthDate 변환
            if (ageGroup != null && !ageGroup.isEmpty()) {
                int age = convertAgeGroupToAge(ageGroup);
                int birthYear = LocalDate.now().getYear() - age;
                user.setBirthDate(LocalDateTime.of(birthYear, 1, 1, 0, 0));
            }
            
            // 주소 설정
            if (region != null && district != null) {
                user.setAddress(region + " " + district);
                user.setRegionLevel1(region);
                user.setRegionLevel2(district);
            }
        }

        // AI 추천 호출
        List<RestaurantDTO> result = recommendService.recommend(user, craving, mood, withWho);

        // 결과 뷰로 전달
        model.addAttribute("restaurants", result);

        return "recommend/menu-recommend-result";
    }
    
    // 연령대를 중간값 나이로 변환
    private int convertAgeGroupToAge(String ageGroup) {
        switch (ageGroup) {
            case "10s": return 15;
            case "20s": return 25;
            case "30s": return 35;
            case "40s": return 45;
            case "50s_plus": return 55;
            default: return 25; // 기본값 25세
        }
    }
}