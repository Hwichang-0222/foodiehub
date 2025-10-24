package org.embed.service.impl;

import java.util.List;

import org.embed.dto.UserDTO;
import org.embed.mapper.UserMapper;
import org.embed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /** 1. 전체 유저 조회 (삭제되지 않은 유저만) */
    @Override
    public List<UserDTO> findAllActiveUsers() {
        return userMapper.findAllActiveUsers();
    }

    /** 2. 개별 유저 조회 (id 기준) */
    @Override
    public UserDTO findById(Long id) {
        return userMapper.findById(id);
    }

    /** 3. 이메일로 조회 (로그인용) */
    @Override
    public UserDTO findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /** 4. 새 유저 등록 */
    @Override
    public int insertUser(UserDTO user) {
        return userMapper.insertUser(user);
    }

    /** 5. 유저 정보 수정 */
    @Override
    public int updateUser(UserDTO user) {
        return userMapper.updateUser(user);
    }

    /** 6. 논리 삭제 (is_deleted = 'Y') */
    @Override
    public int softDeleteUser(Long id) {
        return userMapper.softDeleteUser(id);
    }

    /** 7. 관리자 전용: 권한(Role) 변경 */
    @Override
    public int updateUserRole(Long id, String role) {
        return userMapper.updateUserRole(id, role);
    }

    /** 8. 이메일 중복 확인 */
    @Override
    public boolean existsByEmail(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    /** 9. 로그인 검증 */
    @Override
    public boolean validateLogin(String email, String password) {
        UserDTO user = userMapper.validateLogin(email, password);

        // 유저가 존재하고 탈퇴 상태가 아니며, 비밀번호 일치
        return user != null && "N".equals(user.getIsDeleted()) && user.getPassword().equals(password);
    }

    /** 10. 권한별 유저 조회 (관리자용) */
    @Override
    public List<UserDTO> getUsersByRole(String role) {
        return userMapper.findUsersByRole(role);
    }
}
