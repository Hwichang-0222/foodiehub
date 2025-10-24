package org.embed.mappertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.embed.domain.Role;
import org.embed.dto.UserDTO;
import org.embed.mapper.UserMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    // 테스트용 ID 저장
    private static Long testUserId;

    @Test
    @Order(1)
    void testInsertUser() {
        UserDTO user = new UserDTO();
        user.setEmail("mapper_test@example.com");
        user.setPassword("1234");
        user.setName("매퍼테스트유저");
        user.setProfileImageUrl("https://example.com/test.png");
        user.setProvider("local");

        int result = userMapper.insertUser(user);
        assertEquals(1, result);
        assertNotNull(user.getId());
        testUserId = user.getId();

        System.out.println("1️⃣ 등록된 유저 ID: " + testUserId);
    }

    @Test
    @Order(2)
    void testFindById() {
        UserDTO user = userMapper.findById(testUserId);
        assertNotNull(user);
        assertEquals("mapper_test@example.com", user.getEmail());
        System.out.println("2️⃣ findById 결과: " + user);
    }

    @Test
    @Order(3)
    void testFindByEmail() {
        UserDTO user = userMapper.findByEmail("mapper_test@example.com");
        assertNotNull(user);
        assertEquals("매퍼테스트유저", user.getName());
        System.out.println("3️⃣ findByEmail 결과: " + user);
    }

    @Test
    @Order(4)
    void testFindAllActiveUsers() {
        List<UserDTO> users = userMapper.findAllActiveUsers();
        assertTrue(users.size() > 0);
        System.out.println("4️⃣ findAllActiveUsers 결과 수: " + users.size());
    }

    @Test
    @Order(5)
    void testUpdateUser() {
        UserDTO user = userMapper.findById(testUserId);
        user.setName("수정된테스트유저");
        userMapper.updateUser(user);

        UserDTO updated = userMapper.findById(testUserId);
        assertEquals("수정된테스트유저", updated.getName());
        System.out.println("5️⃣ updateUser 결과: " + updated.getName());
    }

    @Test
    @Order(6)
    void testCountByEmail() {
        int count = userMapper.countByEmail("mapper_test@example.com");
        assertTrue(count > 0);
        System.out.println("6️⃣ countByEmail 결과: " + count);
    }

    @Test
    @Order(7)
    void testValidateLogin() {
        UserDTO user = userMapper.validateLogin("mapper_test@example.com", "1234");
        assertNotNull(user);
        assertEquals("mapper_test@example.com", user.getEmail());
        System.out.println("7️⃣ validateLogin 결과: " + user.getEmail());
    }

    @Test
    @Order(8)
    void testUpdateUserRole() {
        int result = userMapper.updateUserRole(testUserId, "ROLE_ADMIN");
        assertEquals(1, result);

        UserDTO user = userMapper.findById(testUserId);
        assertEquals(Role.ROLE_ADMIN, user.getRole());
        System.out.println("8️⃣ updateUserRole 결과: " + user.getRole());
    }

    @Test
    @Order(9)
    void testSoftDeleteUser() {
        int result = userMapper.softDeleteUser(testUserId);
        assertEquals(1, result);

        UserDTO deleted = userMapper.findById(testUserId);
        assertEquals("Y", deleted.getIsDeleted());
        System.out.println("9️⃣ softDeleteUser 결과: is_deleted=" + deleted.getIsDeleted());
    }

    @Test
    @Order(10)
    void testFindUsersByRole() {
        List<UserDTO> admins = userMapper.findUsersByRole("ROLE_ADMIN");
        assertNotNull(admins);
        System.out.println("10️⃣ findUsersByRole 결과 수: " + admins.size());
    }
}
