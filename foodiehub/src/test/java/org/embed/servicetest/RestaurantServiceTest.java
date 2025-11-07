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

    private static Long testId;

    /* ============================================
       전체 조회
    ============================================ */

    @Test
    @Order(1)
    void testFindAll() {
        List<RestaurantDTO> list = restaurantService.findAll("", "all", 0, 10);
        System.out.println("[1] 전체 조회 개수: " + list.size());
        Assertions.assertNotNull(list);
    }

    /* ============================================
       맛집 등록
    ============================================ */

    @Test
    @Order(2)
    void testInsertRestaurant() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("서비스테스트식당");
        dto.setDescription("Service 테스트용 식당");
        dto.setAddress("서울시 중구 테스트로 1");
        dto.setRegion("서울");
        dto.setCategory("한식");
        dto.setLatitude(37.55);
        dto.setLongitude(126.97);
        dto.setMainImageUrl("/images/service-test.jpg");

        int result = restaurantService.insertRestaurant(dto);
        testId = dto.getId();

        System.out.println("[2] 맛집 등록 - ID: " + testId);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       단일 조회
    ============================================ */

    @Test
    @Order(3)
    void testFindById() {
        RestaurantDTO dto = restaurantService.findById(testId);
        System.out.println("[3] 단일 조회 - 이름: " + dto.getName());
        Assertions.assertNotNull(dto);
    }

    /* ============================================
       맛집 수정
    ============================================ */

    @Test
    @Order(4)
    void testUpdateRestaurant() {
        RestaurantDTO dto = restaurantService.findById(testId);
        dto.setDescription("Service 계층에서 수정 완료");
        int result = restaurantService.updateRestaurant(dto);
        System.out.println("[4] 맛집 수정 결과: " + result);
        Assertions.assertEquals(1, result);
    }

    /* ============================================
       필터 검색
    ============================================ */

    @Test
    @Order(5)
    void testFindByFilter_RegionAndCategory() {
        List<RestaurantDTO> list = restaurantService.findByFilter("서울", "한식", null, 0, 10);
        System.out.println("[5] 서울+한식 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(6)
    void testFindByFilter_Keyword() {
        List<RestaurantDTO> list = restaurantService.findByFilter(null, null, "치킨", 0, 10);
        System.out.println("[6] 키워드 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(7)
    void testFindByFilter_EtcCategory() {
        List<RestaurantDTO> list = restaurantService.findByFilter(null, "기타", null, 0, 10);
        System.out.println("[7] 기타 카테고리 검색 결과: " + list.size());
        list.forEach(r -> System.out.println("  - " + r.getName() + " | " + r.getCategory()));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(8)
    void testCountByFilter() {
        int total = restaurantService.countByFilter("서울", "한식", null);
        System.out.println("[8] 서울+한식 총 개수: " + total);
        Assertions.assertTrue(total >= 0);
    }

    /* ============================================
       관리자 기능
    ============================================ */

    @Test
    @Order(9)
    void testCountAllWithOwner() {
        int total = restaurantService.countAllWithOwner("", "all");
        System.out.println("[9] 전체 식당 수: " + total);
        Assertions.assertTrue(total >= 0);
    }

    @Test
    @Order(10)
    void testUpdateOwner() {
        restaurantService.updateOwner(testId, 1L);
        System.out.println("[10] 오너 지정 완료 - 식당ID: " + testId + ", 오너ID: 1");
    }

    @Test
    @Order(11)
    void testFindAssignedOwnerIds() {
        List<Long> ownerIds = restaurantService.findAssignedOwnerIds();
        System.out.println("[11] 배정된 오너 ID 목록: " + ownerIds);
        Assertions.assertNotNull(ownerIds);
    }

    /* ============================================
       맛집 삭제
    ============================================ */

    @Test
    @Order(12)
    void testDeleteRestaurant() {
        int result = restaurantService.deleteRestaurant(testId);
        System.out.println("[12] 맛집 삭제 결과: " + result);
        Assertions.assertEquals(1, result);
    }
}