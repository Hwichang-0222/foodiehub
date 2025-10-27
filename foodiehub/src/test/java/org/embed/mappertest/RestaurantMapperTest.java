package org.embed.mappertest;

import java.util.List;

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

    // 1. 전체 맛집 조회 테스트
    @Test
    @Order(1)
    void testFindAll() {
        List<RestaurantDTO> list = restaurantMapper.findAll(0, 10);
        System.out.println("1. 전체 조회 결과 개수: " + list.size());
        list.forEach(r -> System.out.println(r.getId() + " | " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    // 2. 맛집 등록 테스트
    @Test
    @Order(2)
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

        System.out.println("2. 등록된 ID: " + testId);
        Assertions.assertEquals(1, result);
    }

    // 3. 단일 조회 테스트
    @Test
    @Order(3)
    void testFindById() {
        RestaurantDTO dto = restaurantMapper.findById(testId);
        System.out.println("3. 단일 조회 결과: " + dto);
        Assertions.assertNotNull(dto);
    }

    // 4. 맛집 수정 테스트
    @Test
    @Order(4)
    void testUpdateRestaurant() {
        RestaurantDTO dto = restaurantMapper.findById(testId);
        dto.setDescription("수정된 설명입니다.");
        dto.setCategory("양식");
        int result = restaurantMapper.updateRestaurant(dto);
        System.out.println("4. 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 5. 맛집 삭제 테스트
    @Test
    @Order(5)
    void testDeleteRestaurant() {
        int result = restaurantMapper.deleteRestaurant(testId);
        System.out.println("5. 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 6. 필터 검색 테스트 (서울 + 한식)
    @Test
    @Order(6)
    void testFindByFilter_withRegionAndCategory() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter("서울", "한식", null, 0, 10);
        System.out.println("6. 서울 + 한식 검색 결과: " + list.size());
        list.forEach(r -> System.out.println(r.getName() + " | " + r.getCategory() + " | " + r.getRegion()));
        Assertions.assertNotNull(list);
    }

    // 7. 키워드 검색 테스트 (치킨)
    @Test
    @Order(7)
    void testFindByFilter_withKeyword() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter(null, null, "치킨", 0, 10);
        System.out.println("7. 키워드 '치킨' 검색 결과: " + list.size());
        list.forEach(r -> System.out.println(r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    // 8. 기타 카테고리 검색 테스트
    @Test
    @Order(8)
    void testFindByFilter_withEtcCategory() {
        List<RestaurantDTO> list = restaurantMapper.findByFilter(null, "기타", null, 0, 10);
        System.out.println("8. 기타 카테고리 검색 결과: " + list.size());
        list.forEach(r -> System.out.println(r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    // 9. 총 개수(countByFilter) 테스트
    @Test
    @Order(9)
    void testCountByFilter() {
        int total = restaurantMapper.countByFilter("서울", "한식", null);
        System.out.println("9. 서울 + 한식 총 개수: " + total);
        Assertions.assertTrue(total >= 0);
    }
}
