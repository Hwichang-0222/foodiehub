package org.embed.mappertest;

import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.mapper.RestaurantMapper;
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

    private static Long testRestaurantId;

    // 1. 전체 목록 조회
    @Test
    @Order(1)
    void testFindAll() {
        System.out.println("[1] 전체 맛집 목록 조회 테스트");
        List<RestaurantDTO> list = restaurantMapper.findAll();
        list.forEach(System.out::println);
    }

    // 2. 맛집 등록
    @Test
    @Order(2)
    void testInsertRestaurant() {
        System.out.println("[2] 맛집 등록 테스트");

        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("테스트 맛집");
        dto.setDescription("테스트용 맛집 설명입니다.");
        dto.setAddress("서울시 강남구 어딘가");
        dto.setRegion("서울");
        dto.setCategory("한식");
        dto.setLatitude(37.500);
        dto.setLongitude(127.030);
        dto.setMainImageUrl("test_image.jpg");

        int result = restaurantMapper.insertRestaurant(dto);
        testRestaurantId = dto.getId();

        System.out.println("등록 결과: " + result);
        System.out.println("생성된 ID: " + testRestaurantId);
    }

    // 3. 개별 맛집 조회
    @Test
    @Order(3)
    void testFindById() {
        System.out.println("[3] 개별 맛집 조회 테스트");
        RestaurantDTO restaurant = restaurantMapper.findById(testRestaurantId);
        System.out.println(restaurant);
    }

    // 4. 맛집 수정
    @Test
    @Order(4)
    void testUpdateRestaurant() {
        System.out.println("[4] 맛집 수정 테스트");

        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(testRestaurantId);
        dto.setName("수정된 맛집 이름");
        dto.setDescription("수정된 설명입니다.");
        dto.setAddress("서울시 송파구 수정로 123");
        dto.setRegion("서울");
        dto.setCategory("양식");
        dto.setLatitude(37.512);
        dto.setLongitude(127.102);
        dto.setMainImageUrl("updated_image.jpg");

        int result = restaurantMapper.updateRestaurant(dto);
        System.out.println("수정 결과: " + result);
    }

    // 5. 이름 검색 (LIKE)
    @Test
    @Order(5)
    void testFindByName() {
        System.out.println("[5] 이름 검색 테스트");
        List<RestaurantDTO> list = restaurantMapper.findByName("테스트");
        list.forEach(r -> System.out.println("검색 결과: " + r.getName()));
    }

    // 6. 지역별 검색
    @Test
    @Order(6)
    void testFindByRegion() {
        System.out.println("[6] 필터 검색 테스트");
        List<RestaurantDTO> list = restaurantMapper.findByFilter("서울","");
        list.forEach(r -> System.out.println("지역 결과: " + r.getName() + " / " + r.getRegion()));
        list = restaurantMapper.findByFilter("","한식");
        list.forEach(r -> System.out.println("지역 결과: " + r.getName() + " / " + r.getRegion()));
        list = restaurantMapper.findByFilter("서울","한식");
        list.forEach(r -> System.out.println("지역 결과: " + r.getName() + " / " + r.getRegion()));
    }

    // 7. 삭제 테스트
    @Test
    @Order(8)
    void testDeleteRestaurant() {
        System.out.println("[8] 맛집 삭제 테스트");
        int result = restaurantMapper.deleteRestaurant(testRestaurantId);
        System.out.println("삭제 결과: " + result);
    }
}
