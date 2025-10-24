package org.embed.servicetest;

import org.embed.dto.UserDTO;
import org.embed.domain.Role;
import org.embed.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private static Long testUserId;

    @Test
    @Order(1)
    void testInsertUser() {
        UserDTO user = new UserDTO();
        user.setEmail("service_user@example.com");
        user.setPassword("1234");
        user.setName("서비스테스트유저");
        user.setProfileImageUrl("https://example.com/profile.png");
        user.setProvider("local");
        user.setRole(Role.ROLE_USER);
        user.setIsDeleted("N");

        int result = userService.insertUser(user);
        assertEquals(1, result);
        assertNotNull(user.getId());

        testUserId = user.getId();
        System.out.println("1. insertUser: id=" + testUserId);
    }

    @Test
    @Order(2)
    void testFindById() {
        UserDTO user = userService.findById(testUserId);
        assertNotNull(user);
        assertEquals("service_user@example.com", user.getEmail());
        System.out.println("2. findById: " + user);
    }

    @Test
    @Order(3)
    void testFindByEmail() {
        UserDTO user = userService.findByEmail("service_user@example.com");
        assertNotNull(user);
        assertEquals("서비스테스트유저", user.getName());
        System.out.println("3. findByEmail: " + user.getName());
    }

    @Test
    @Order(4)
    void testFindAllActiveUsers() {
        List<UserDTO> users = userService.findAllActiveUsers();
        assertTrue(users.size() > 0);
        System.out.println("4. findAllActiveUsers count: " + users.size());
    }

    @Test
    @Order(5)
    void testUpdateUser() {
        UserDTO user = userService.findById(testUserId);
        user.setName("수정된유저");
        int result = userService.updateUser(user);
        assertEquals(1, result);

        UserDTO updated = userService.findById(testUserId);
        assertEquals("수정된유저", updated.getName());
        System.out.println("5. updateUser: " + updated.getName());
    }

    @Test
    @Order(6)
    void testExistsByEmail() {
        boolean exists = userService.existsByEmail("service_user@example.com");
        assertTrue(exists);
        System.out.println("6. existsByEmail: " + exists);
    }

    @Test
    @Order(7)
    void testValidateLogin() {
        boolean valid = userService.validateLogin("service_user@example.com", "1234");
        assertTrue(valid);
        System.out.println("7. validateLogin: " + valid);
    }

    @Test
    @Order(8)
    void testUpdateUserRole() {
        int result = userService.updateUserRole(testUserId, "ROLE_ADMIN");
        assertEquals(1, result);

        UserDTO user = userService.findById(testUserId);
        assertEquals(Role.ROLE_ADMIN, user.getRole());
        System.out.println("8. updateUserRole: " + user.getRole());
    }

    @Test
    @Order(9)
    void testSoftDeleteUser() {
        int result = userService.softDeleteUser(testUserId);
        assertEquals(1, result);

        UserDTO deleted = userService.findById(testUserId);
        assertEquals("Y", deleted.getIsDeleted());
        System.out.println("9. softDeleteUser: is_deleted=" + deleted.getIsDeleted());
    }

    @Test
    @Order(10)
    void testGetUsersByRole() {
        List<UserDTO> admins = userService.getUsersByRole("ROLE_ADMIN");
        assertNotNull(admins);
        System.out.println("10. getUsersByRole count: " + admins.size());
    }
}
