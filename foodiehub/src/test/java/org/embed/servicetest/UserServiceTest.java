package org.embed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.dto.UserDTO;
import org.embed.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private static Long testUserId;

    /* ============================================
       사용자 등록
    ============================================ */

    @Test
    @Order(1)
    void testInsertUser() {
        UserDTO user = new UserDTO();
        user.setEmail("service_user@example.com");
        user.setPassword("1234");
        user.setName("서비스테스트유저");
        user.setProfileImageUrl("https://example.com/profile.png");
        user.setProvider("local");
        user.setRole("ROLE_USER");
        user.setIsDeleted("N");

        int result = userService.insertUser(user);
        assertEquals(1, result);
        assertNotNull(user.getId());

        testUserId = user.getId();
        System.out.println("[1] insertUser - ID: " + testUserId);
    }

    /* ============================================
       사용자 조회
    ============================================ */

    @Test
    @Order(2)
    void testFindById() {
        UserDTO user = userService.findById(testUserId);
        assertNotNull(user);
        assertEquals("service_user@example.com", user.getEmail());
        System.out.println("[2] findById - 이름: " + user.getName());
    }

    @Test
    @Order(3)
    void testFindByEmail() {
        UserDTO user = userService.findByEmail("service_user@example.com");
        assertNotNull(user);
        assertEquals("서비스테스트유저", user.getName());
        System.out.println("[3] findByEmail - 이름: " + user.getName());
    }

    @Test
    @Order(4)
    void testFindByPhoneAndName() {
        UserDTO user = userService.findById(testUserId);
        user.setPhone("010-9999-8888");
        userService.updateUser(user);

        UserDTO found = userService.findByPhoneAndName("010-9999-8888", "서비스테스트유저");
        assertNotNull(found);
        assertEquals("service_user@example.com", found.getEmail());
        System.out.println("[4] findByPhoneAndName - 이메일: " + found.getEmail());
    }

    /* ============================================
       사용자 수정
    ============================================ */

    @Test
    @Order(5)
    void testUpdateUser() {
        UserDTO user = userService.findById(testUserId);
        user.setName("수정된유저");
        int result = userService.updateUser(user);
        assertEquals(1, result);

        UserDTO updated = userService.findById(testUserId);
        assertEquals("수정된유저", updated.getName());
        System.out.println("[5] updateUser - 이름: " + updated.getName());
    }

    /* ============================================
       이메일 중복 확인
    ============================================ */

    @Test
    @Order(6)
    void testCountByEmail() {
        int count = userService.countByEmail("service_user@example.com");
        assertEquals(1, count);
        System.out.println("[6] countByEmail - 결과: " + count);
    }

    /* ============================================
       로그인 검증
    ============================================ */

    @Test
    @Order(7)
    void testValidateLogin() {
        boolean valid = userService.validateLogin("service_user@example.com", "1234");
        assertTrue(valid);
        System.out.println("[7] validateLogin - 결과: " + valid);
    }

    /* ============================================
       권한 변경
    ============================================ */

    @Test
    @Order(8)
    void testUpdateUserRole() {
        int result = userService.updateUserRole(testUserId, "ROLE_ADMIN");
        assertEquals(1, result);

        UserDTO user = userService.findById(testUserId);
        assertEquals("ROLE_ADMIN", user.getRole());
        System.out.println("[8] updateUserRole - 권한: " + user.getRole());
    }

    /* ============================================
       권한별 조회
    ============================================ */

    @Test
    @Order(9)
    void testFindByRole() {
        List<UserDTO> admins = userService.findByRole("ROLE_ADMIN");
        assertNotNull(admins);
        assertTrue(admins.size() > 0);
        System.out.println("[9] findByRole - 결과 수: " + admins.size());
    }

    /* ============================================
       회원 검색
    ============================================ */

    @Test
    @Order(10)
    void testSearchUsers() {
        List<UserDTO> users = userService.searchUsers("테스트", "N", "ROLE_ADMIN", 0, 10);
        assertNotNull(users);
        System.out.println("[10] searchUsers - 결과 수: " + users.size());
    }

    @Test
    @Order(11)
    void testCountSearchUsers() {
        int count = userService.countSearchUsers("테스트", "N", "ROLE_ADMIN");
        assertTrue(count >= 0);
        System.out.println("[11] countSearchUsers - 결과: " + count);
    }

    /* ============================================
       논리 삭제
    ============================================ */

    @Test
    @Order(12)
    void testSoftDeleteUser() {
        int result = userService.softDeleteUser(testUserId);
        assertEquals(1, result);

        UserDTO deleted = userService.findById(testUserId);
        assertEquals("Y", deleted.getIsDeleted());
        System.out.println("[12] softDeleteUser - is_deleted: " + deleted.getIsDeleted());
    }
}