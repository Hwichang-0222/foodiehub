package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.dto.RestaurantDTO;
import org.embed.service.RestaurantService;
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

    @Test
    @Order(1)
    void testInsertRestaurant() {
        RestaurantDTO restaurant = new RestaurantDTO();
        restaurant.setName("테스트 맛집");
        restaurant.setDescription("서비스 테스트용 맛집입니다.");
        restaurant.setAddress("서울 강남구 테스트로 123");
        restaurant.setRegion("서울");
        restaurant.setCategory("한식");
        restaurant.setLatitude(37.1234);
        restaurant.setLongitude(127.9876);
        restaurant.setMainImageUrl("https://example.com/test.jpg");

        int result = restaurantService.insertRestaurant(restaurant);
        assertEquals(1, result);
        assertNotNull(restaurant.getId());

        testRestaurantId = restaurant.getId();
        System.out.println("1. insertRestaurant: ID=" + testRestaurantId);
    }

    @Test
    @Order(2)
    void testFindById() {
        RestaurantDTO restaurant = restaurantService.findById(testRestaurantId);
        assertNotNull(restaurant);
        assertEquals("테스트 맛집", restaurant.getName());
        System.out.println("2. findById: " + restaurant.getName());
    }

    @Test
    @Order(3)
    void testUpdateRestaurant() {
        RestaurantDTO restaurant = restaurantService.findById(testRestaurantId);
        restaurant.setName("수정된 맛집 이름");
        int result = restaurantService.updateRestaurant(restaurant);
        assertEquals(1, result);

        RestaurantDTO updated = restaurantService.findById(testRestaurantId);
        assertEquals("수정된 맛집 이름", updated.getName());
        System.out.println("3. updateRestaurant: " + updated.getName());
    }

    @Test
    @Order(4)
    void testFindAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.findAll();
        assertTrue(restaurants.size() > 0);
        System.out.println("4. findAllRestaurants count: " + restaurants.size());
    }

    @Test
    @Order(5)
    void testFindByFilter_RegionOnly() {
        List<RestaurantDTO> result = restaurantService.findByFilter("서울", "");
        assertNotNull(result);
        System.out.println("5. findByFilter (지역만): " + result.size());
    }

    @Test
    @Order(6)
    void testFindByFilter_CategoryOnly() {
        List<RestaurantDTO> result = restaurantService.findByFilter("", "한식");
        assertNotNull(result);
        System.out.println("6. findByFilter (카테고리만): " + result.size());
    }

    @Test
    @Order(7)
    void testFindByFilter_Both() {
        List<RestaurantDTO> result = restaurantService.findByFilter("서울", "한식");
        assertNotNull(result);
        System.out.println("7. findByFilter (지역+카테고리): " + result.size());
    }

    @Test
    @Order(8)
    void testFindByFilter_All() {
        List<RestaurantDTO> result = restaurantService.findByFilter("", "");
        assertNotNull(result);
        System.out.println("8. findByFilter (전체): " + result.size());
    }

    @Test
    @Order(9)
    void testFindByName() {
        List<RestaurantDTO> result = restaurantService.findByName("맛집");
        assertNotNull(result);
        System.out.println("9. findByName 결과: " + result.size());
    }

    @Test
    @Order(10)
    void testDeleteRestaurant() {
        int result = restaurantService.deleteRestaurant(testRestaurantId);
        assertEquals(1, result);

        RestaurantDTO deleted = restaurantService.findById(testRestaurantId);
        assertNull(deleted);
        System.out.println("10. deleteRestaurant 완료");
    }
}
