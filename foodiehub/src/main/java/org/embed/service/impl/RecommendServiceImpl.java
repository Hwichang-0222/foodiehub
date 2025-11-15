package org.embed.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

        // 1. 전체 식당 목록 불러오기
        List<RestaurantDTO> allRestaurants = restaurantService.findAllForAI();

        // 2. 사용자 지역 정보
        String userRegion1 = user.getRegionLevel1();
        String userRegion2 = user.getRegionLevel2();
        String userRegion3 = user.getRegionLevel3();

        // 3. 사용자 지역과 동일한 식당만 필터링
        List<RestaurantDTO> candidates = allRestaurants.stream()
            .filter(r -> Objects.equals(r.getRegionLevel1(), userRegion1))
            .filter(r -> Objects.equals(r.getRegionLevel2(), userRegion2))
            .collect(Collectors.toList());

        log.debug("후보 식당 수 = {}", candidates.size());

        // 후보가 없다면 빈 리스트 반환
        if (candidates.isEmpty()) {
            log.warn("추천 가능한 지역 식당 없음");
            return List.of();
        }

        // 4. 각 후보 식당에 메뉴 정보 추가
        for (RestaurantDTO r : candidates) {
            r.setMenus(menuService.findByRestaurantId(r.getId()));
        }

        // 5. JSON 변환
        String restaurantJson;
        try {
            restaurantJson = objectMapper.writeValueAsString(candidates);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
            return List.of();
        }

        // 6. 나이 계산
        int age = calculateAge(user.getBirthDate());

        // 7. 프롬프트 구성 (이모지 없음)
        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 한국 사용자에게 맛집을 추천하는 AI다.\n");
        prompt.append("출력 형식은 반드시 숫자 배열만 가능하다.\n");
        prompt.append("예: [3,7,15]\n");
        prompt.append("설명, 문장, 소개글, 목록, 마크다운, 다른 텍스트 어떤 것도 작성하면 안 된다.\n");
        prompt.append("만약 배열 외 텍스트가 있다면 잘못된 응답이다.\n\n");

        prompt.append("조건:\n");
        prompt.append("- candidate JSON 안에 존재하는 restaurantId만 사용해라.\n");
        prompt.append("- 존재하지 않는 ID를 생성하거나 추측해서는 절대 안 된다.\n");
        prompt.append("- 최대 3개 이하 식당만 반환해라.\n");
        prompt.append("- 후보가 3개보다 적으면 존재하는 개수만큼만 반환해라.\n");
        prompt.append("- 배열은 정수만 포함해야 한다.\n\n");


        prompt.append("User Info:\n");
        prompt.append("- age: ").append(age).append("\n");
        prompt.append("- gender: ").append(user.getGender()).append("\n");
        prompt.append("- address: ").append(user.getAddress()).append("\n");
        prompt.append("- craving: ").append(craving).append("\n");
        prompt.append("- mood: ").append(mood).append("\n");
        prompt.append("- withWho: ").append(withWho).append("\n\n");

        prompt.append("Candidate Restaurants(JSON):\n");
        prompt.append(restaurantJson).append("\n");

        // 8. AI 호출
        String aiResponse = clovaApiService.generateSummary(prompt.toString());
        log.debug("AI 응답: {}", aiResponse);

        // 9. 숫자만 추출하여 ID 리스트 생성
        String idStr = aiResponse.replaceAll("[^0-9,]", "");
        String[] arr = idStr.split(",");

        Set<Long> idSet = new HashSet<>();
        for (String s : arr) {
            try {
                idSet.add(Long.parseLong(s.trim()));
            } catch (Exception ignore) {}
        }

        // 10. 실제 존재하는 후보 식당만 결과에 포함
        List<RestaurantDTO> result = new ArrayList<>();
        for (RestaurantDTO r : candidates) {
            if (idSet.contains(r.getId())) {
                result.add(r);
            }
        }

        log.debug("최종 추천 결과 개수 = {}", result.size());
        return result;
    }

}
