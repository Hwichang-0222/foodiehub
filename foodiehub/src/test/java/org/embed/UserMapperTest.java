package org.embed;

import org.embed.dto.UserDTO;
import org.embed.domain.Role;
import org.embed.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserMapperTest {

	@Autowired
    private UserMapper userMapper;

    private static Long testUserId;
    
    // 1. 전체 유저 조회 (관리자 포함)
    @Test
    @Order(1)
    void testFindAllActiveUsers() {
        System.out.println("🧩 [1] 전체 유저 조회 테스트");
        List<UserDTO> users = userMapper.findAllActiveUsers();
        users.forEach(System.out::println);
    }

    // 2. 새 유저 등록 (기본 ROLE_USER)
    @Test
    @Order(2)
    void testInsertUser() {
        System.out.println("🧩 [2] 새 유저 등록 테스트");

        UserDTO user = new UserDTO();
        user.setEmail("user_test@example.com");
        user.setPassword("pass1234");
        user.setName("테스트유저");
        user.setProfileImageUrl(null);
        user.setProvider("local");
        user.setRole(Role.ROLE_USER);
        user.setIsDeleted("N");

        int result = userMapper.insertUser(user);
        testUserId = user.getId();

        System.out.println("삽입된 행 수: " + result);
        System.out.println("생성된 유저 ID: " + testUserId);
    }

    // 3. 개별 유저 조회
    @Test
    @Order(3)
    void testFindById() {
        System.out.println("🧩 [3] 개별 유저 조회 테스트");
        UserDTO user = userMapper.findById(testUserId);
        System.out.println(user);
    }

    // 4. 유저 정보 수정 (본인용)
    @Test
    @Order(4)
    void testUpdateUser() {
        System.out.println("🧩 [4] 유저 정보 수정 테스트");

        UserDTO user = new UserDTO();
        user.setId(testUserId);
        user.setPassword("newpass");
        user.setName("이수정");
        user.setProfileImageUrl("profile_img_updated.png");

        int result = userMapper.updateUser(user);
        System.out.println("수정된 행 수: " + result);
    }

    // 5. 관리자 전용 Role 변경 (ROLE_USER → ROLE_OWNER)
    @Test
    @Order(5)
    void testUpdateUserRole() {
        System.out.println("🧩 [5] Role 변경 테스트 (관리자 전용)");

        int result = userMapper.updateUserRole(2L, "ROLE_OWNER");
        System.out.println("Role 변경된 행 수: " + result);

        UserDTO updated = userMapper.findById(2L);
        System.out.println("변경 후 Role: " + updated.getRole());
    }

    // 6. 논리 삭제 (is_deleted = 'Y')
    @Test
    @Order(6)
    void testSoftDeleteUser() {
        System.out.println("🧩 [6] 논리 삭제 테스트");

        int result = userMapper.softDeleteUser(testUserId);
        System.out.println("삭제된 행 수: " + result);

        UserDTO deleted = userMapper.findById(testUserId);
        System.out.println("삭제 후 조회 결과: " + deleted);
    }

}
