package org.embed.mappertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

    private static Long testUserId;

    /* ============================================
       사용자 등록
    ============================================ */

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

        System.out.println("[1] 사용자 등록 - ID: " + testUserId);
    }

    /* ============================================
       사용자 조회
    ============================================ */

    @Test
    @Order(2)
    void testFindById() {
        UserDTO user = userMapper.findById(testUserId);
        assertNotNull(user);
        assertEquals("mapper_test@example.com", user.getEmail());
        System.out.println("[2] findById 결과: " + user.getName());
    }

    @Test
    @Order(3)
    void testFindByEmail() {
        UserDTO user = userMapper.findByEmail("mapper_test@example.com");
        assertNotNull(user);
        assertEquals("매퍼테스트유저", user.getName());
        System.out.println("[3] findByEmail 결과: " + user.getName());
    }

    @Test
    @Order(4)
    void testFindByPhoneAndName() {
        UserDTO user = userMapper.findById(testUserId);
        user.setPhone("010-1234-5678");
        userMapper.updateUser(user);

        UserDTO found = userMapper.findByPhoneAndName("010-1234-5678", "매퍼테스트유저");
        assertNotNull(found);
        assertEquals("mapper_test@example.com", found.getEmail());
        System.out.println("[4] findByPhoneAndName 결과: " + found.getEmail());
    }

    /* ============================================
       사용자 수정
    ============================================ */

    @Test
    @Order(5)
    void testUpdateUser() {
        UserDTO user = userMapper.findById(testUserId);
        user.setName("수정된테스트유저");
        userMapper.updateUser(user);

        UserDTO updated = userMapper.findById(testUserId);
        assertEquals("수정된테스트유저", updated.getName());
        System.out.println("[5] updateUser 결과: " + updated.getName());
    }

    /* ============================================
       이메일 중복 확인
    ============================================ */

    @Test
    @Order(6)
    void testCountByEmail() {
        int count = userMapper.countByEmail("mapper_test@example.com");
        assertTrue(count > 0);
        System.out.println("[6] countByEmail 결과: " + count);
    }

    /* ============================================
       로그인 검증
    ============================================ */

    @Test
    @Order(7)
    void testValidateLogin() {
        UserDTO user = userMapper.validateLogin("mapper_test@example.com", "1234");
        assertNotNull(user);
        assertEquals("mapper_test@example.com", user.getEmail());
        System.out.println("[7] validateLogin 결과: " + user.getEmail());
    }

    /* ============================================
       권한 변경
    ============================================ */

    @Test
    @Order(8)
    void testUpdateUserRole() {
        int result = userMapper.updateUserRole(testUserId, "ROLE_ADMIN");
        assertEquals(1, result);

        UserDTO user = userMapper.findById(testUserId);
        assertEquals("ROLE_ADMIN", user.getRole());
        System.out.println("[8] updateUserRole 결과: " + user.getRole());
    }

    /* ============================================
       권한별 조회
    ============================================ */

    @Test
    @Order(9)
    void testFindByRole() {
        List<UserDTO> admins = userMapper.findByRole("ROLE_ADMIN");
        assertNotNull(admins);
        assertTrue(admins.size() > 0);
        System.out.println("[9] findByRole 결과 수: " + admins.size());
    }

    /* ============================================
       회원 검색
    ============================================ */

    @Test
    @Order(10)
    void testSearchUsers() {
        List<UserDTO> users = userMapper.searchUsers("테스트", "N", "ROLE_ADMIN", 0, 10);
        assertNotNull(users);
        System.out.println("[10] searchUsers 결과 수: " + users.size());
    }

    @Test
    @Order(11)
    void testCountSearchUsers() {
        int count = userMapper.countSearchUsers("테스트", "N", "ROLE_ADMIN");
        assertTrue(count >= 0);
        System.out.println("[11] countSearchUsers 결과: " + count);
    }

    /* ============================================
       논리 삭제
    ============================================ */

    @Test
    @Order(12)
    void testSoftDeleteUser() {
        int result = userMapper.softDeleteUser(testUserId);
        assertEquals(1, result);

        UserDTO deleted = userMapper.findById(testUserId);
        assertEquals("Y", deleted.getIsDeleted());
        System.out.println("[12] softDeleteUser 결과: is_deleted=" + deleted.getIsDeleted());
    }
}