package org.embed.mappertest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.embed.dto.RestaurantDTO;
import org.embed.mapper.RestaurantMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestaurantMapperTest {

    @Autowired
    private RestaurantMapper restaurantMapper;

    private static Long testId;

    /* ============================================
       맛집 등록
    ============================================ */

    @Test
    @Order(1)
    void testInsertRestaurant() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("테스트식당");
        dto.setDescription("JUnit 테스트용 식당입니다.");
        dto.setAddress("서울시 테스트구 테스트동 123");
        dto.setRegion("서울");
        dto.setCategory("한식");
        dto.setLatitude(37.55);
        dto.setLongitude(126.97);
        dto.setMainImageUrl("/images/test.jpg");

        int result = restaurantMapper.insertRestaurant(dto);
        testId = dto.getId();

        System.out.println("[1] 맛집 등록 - ID: " + testId);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       맛집 조회
    ============================================ */

    @Test
    @Order(2)
    void testFindById() {
        RestaurantDTO dto = restaurantMapper.findById(testId);
        System.out.println("[2] 단일 조회 결과: " + dto.getName());
        Assertions.assertNotNull(dto);
    }

    @Test
    @Order(3)
    void testFindAll() {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("limit", 10);
        params.put("keyword", "");
        params.put("ownerFilter", "all");

        List<RestaurantDTO> list = restaurantMapper.findAll(params);
        System.out.println("[3] 전체 조회 결과 개수: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getId() + " | " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    /* ============================================
       필터 검색
    ============================================ */

    @Test
    @Order(4)
    void testFindByFilter_withRegionAndCategory() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter("서울", "한식", null, 0, 10);
        System.out.println("[4] 서울+한식 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName() + " | " + r.getCategory() + " | " + r.getRegion()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(5)
    void testFindByFilter_withKeyword() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter(null, null, "치킨", 0, 10);
        System.out.println("[5] 키워드 '치킨' 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(6)
    void testFindByFilter_withEtcCategory() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter(null, "기타", null, 0, 10);
        System.out.println("[6] 기타 카테고리 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(7)
    void testCountByFilter() {
        int total = restaurantMapper.countByFilter("서울", "한식", null);
        System.out.println("[7] 서울+한식 총 개수: " + total);
        Assertions.assertTrue(total >= 0);
    }

    /* ============================================
       맛집 수정
    ============================================ */

    @Test
    @Order(8)
    void testUpdateRestaurant() {
        RestaurantDTO dto = restaurantMapper.findById(testId);
        dto.setDescription("수정된 설명입니다.");
        dto.setCategory("양식");
        int result = restaurantMapper.updateRestaurant(dto);
        System.out.println("[8] 맛집 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       관리자 기능
    ============================================ */

    @Test
    @Order(9)
    void testCountAllWithOwner() {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "");
        params.put("ownerFilter", "all");

        int total = restaurantMapper.countAllWithOwner(params);
        System.out.println("[9] 전체 식당 수: " + total);
        Assertions.assertTrue(total >= 0);
    }

    @Test
    @Order(10)
    void testUpdateOwner() {
        Map<String, Object> params = new HashMap<>();
        params.put("restaurantId", testId);
        params.put("ownerId", 62L);

        restaurantMapper.updateOwner(params);
        System.out.println("[10] 오너 지정 완료 - 식당ID: " + testId + ", 오너ID: 1");
    }

    @Test
    @Order(11)
    void testFindAssignedOwnerIds() {
        List<Long> ownerIds = restaurantMapper.findAssignedOwnerIds();
        System.out.println("[11] 배정된 오너 ID 목록: " + ownerIds);
        Assertions.assertNotNull(ownerIds);
    }

    /* ============================================
       맛집 삭제
    ============================================ */

    @Test
    @Order(12)
    void testDeleteRestaurant() {
        int result = restaurantMapper.deleteRestaurant(testId);
        System.out.println("[12] 맛집 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}