package org.embed.servicetest;

import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.service.RestaurantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestaurantServiceTest {

    @Autowired
    private RestaurantService restaurantService;

    private static Long testRestaurantId;

    // 1. 맛집 등록
    @Test
    @Order(1)
    void testInsertRestaurant() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("서비스 테스트 맛집");
        dto.setDescription("서비스 계층 등록 테스트용");
        dto.setAddress("서울시 강남구 테스트로 456");
        dto.setRegion("서울");
        dto.setCategory("분식");
        dto.setLatitude(37.50);
        dto.setLongitude(127.02);
        dto.setMainImageUrl("/img/service_test.jpg");

        int result = restaurantService.insertRestaurant(dto);
        testRestaurantId = dto.getId();

        System.out.println("1. 등록 결과: " + result + ", ID: " + testRestaurantId);
        Assertions.assertEquals(1, result);
    }

    // 2. 개별 조회
    @Test
    @Order(2)
    void testFindById() {
        RestaurantDTO dto = restaurantService.findById(testRestaurantId);
        System.out.println("2. 조회 결과: " + dto.getName());
        Assertions.assertNotNull(dto);
    }

    // 3. 전체 목록 조회
    @Test
    @Order(3)
    void testFindAll() {
        List<RestaurantDTO> list = restaurantService.findAll(0, 5);
        System.out.println("3. 전체 조회 개수: " + list.size());
        Assertions.assertTrue(list.size() > 0);
    }

    // 4. 지역/카테고리 검색
    @Test
    @Order(4)
    void testFindByFilter() {
        List<RestaurantDTO> list = restaurantService.findByFilter("서울", "분식", 0, 5);
        System.out.println("4. 필터 검색 결과: " + list.size());
        Assertions.assertNotNull(list);
    }

    // 5. 이름 검색
    @Test
    @Order(5)
    void testFindByName() {
        List<RestaurantDTO> list = restaurantService.findByName("테스트", 0, 5);
        System.out.println("5. 이름 검색 결과: " + list.size());
        Assertions.assertNotNull(list);
    }

    // 6. 수정
    @Test
    @Order(6)
    void testUpdateRestaurant() {
        RestaurantDTO dto = restaurantService.findById(testRestaurantId);
        dto.setDescription("6. 서비스 수정 테스트용 설명");
        int result = restaurantService.updateRestaurant(dto);
        System.out.println("6. 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    // 7. 총 개수 조회
    @Test
    @Order(7)
    void testCountRestaurants() {
        int count = restaurantService.countRestaurants();
        System.out.println("7. 총 맛집 개수: " + count);
        Assertions.assertTrue(count > 0);
    }

    // 8. 삭제
    @Test
    @Order(8)
    void testDeleteRestaurant() {
        int result = restaurantService.deleteRestaurant(testRestaurantId);
        System.out.println("8. 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}
