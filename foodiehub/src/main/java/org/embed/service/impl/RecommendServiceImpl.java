package org.embed.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.dto.UserDTO;
import org.embed.service.ClovaApiService;
import org.embed.service.MenuService;
import org.embed.service.RestaurantService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceImpl {

    private final ClovaApiService clovaApiService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    // ===== 나이 계산 메서드 =====
    private int calculateAge(LocalDateTime birthDateTime) {
        if (birthDateTime == null) return 0;

        LocalDate birth = birthDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        return Period.between(birth, today).getYears();
    }


    // ===== 추천 메인 메서드 =====
    public List<RestaurantDTO> recommend(UserDTO user,
                                         String craving,
                                         String mood,
                                         String withWho) {

        // 1. 전체 식당 목록 가져오기 (findAllForAI 없으므로)
        List<RestaurantDTO> restaurants = restaurantService.findAllForAI();

        // 1-1. 각 식당 메뉴 붙이기
        for (RestaurantDTO r : restaurants) {
            r.setMenus(menuService.findByRestaurantId(r.getId()));
        }

        if (restaurants.isEmpty()) {
            log.warn("식당 데이터 없음");
            return List.of();
        }

        // 2. JSON 변환
        String restaurantJson;
        try {
            restaurantJson = objectMapper.writeValueAsString(restaurants);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
            return List.of();
        }

        // 3. 나이 계산
        int age = calculateAge(user.getBirthDate());

        // 4. 프롬프트 구성 (리뷰 요약 패턴 동일)
        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 한국 사용자에게 맛집을 추천하는 AI다.\n");
        prompt.append("아래 사용자 정보와 식당 리스트(JSON)를 분석해서\n");
        prompt.append("restaurantId만 3개 배열로 반환해라. 예: [3, 7, 15]\n\n");

        prompt.append("User Info:\n");
        prompt.append("- age: ").append(age).append("\n");
        prompt.append("- gender: ").append(user.getGender()).append("\n");
        prompt.append("- address: ").append(user.getAddress()).append("\n");
        prompt.append("- craving: ").append(craving).append("\n");
        prompt.append("- mood: ").append(mood).append("\n");
        prompt.append("- withWho: ").append(withWho).append("\n\n");

        prompt.append("Restaurant JSON:\n");
        prompt.append(restaurantJson).append("\n");

        // 5. Clova 호출 (리뷰 요약 패턴 그대로)
        String aiResponse = clovaApiService.generateSummary(prompt.toString());
        log.debug("AI 응답: {}", aiResponse);

        // 6. 식당 ID만 추출
        String idStr = aiResponse.replaceAll("[^0-9,]", "");
        String[] arr = idStr.split(",");

        List<Long> ids = new ArrayList<>();
        for (String s : arr) {
            try { ids.add(Long.parseLong(s.trim())); } catch (Exception ignore) {}
        }

        // 7. ID로 식당 매칭
        List<RestaurantDTO> result = new ArrayList<>();
        for (RestaurantDTO r : restaurants) {
            if (ids.contains(r.getId())) result.add(r);
        }

        return result;
    }
}
