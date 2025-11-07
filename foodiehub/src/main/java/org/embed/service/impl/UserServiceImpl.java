package org.embed.service.impl;

import java.util.List;

import org.embed.dto.UserDTO;
import org.embed.mapper.UserMapper;
import org.embed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    /* ============================================
       BCrypt 비밀번호 암호화
    ============================================ */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ======================================
       기본 CRUD
    ====================================== */
    // 1. 개별 유저 조회 (id 기준)
    @Override
    public UserDTO findById(Long id) {
        return userMapper.findById(id);
    }

    // 2. 이메일로 조회
    @Override
    public UserDTO findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    // 3. 새 유저 등록 - 비밀번호 암호화
    @Override
    public int insertUser(UserDTO user) {
        // 비밀번호 BCrypt 암호화
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        return userMapper.insertUser(user);
    }

    // 4. 유저 정보 수정
    @Override
    public int updateUser(UserDTO user) {
        // 새 비밀번호가 있으면 암호화
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        return userMapper.updateUser(user);
    }

    // 5. 논리 삭제 (is_deleted = 'Y')
    @Override
    public int softDeleteUser(Long id) {
        return userMapper.softDeleteUser(id);
    }

    /* ======================================
       인증 관련
    ====================================== */
    // 6. 이메일 중복 확인
    @Override
    public int countByEmail(String email) {
        return userMapper.countByEmail(email);
    }

    // 7. 로그인 검증 - BCrypt 비밀번호 비교
    @Override
    public boolean validateLogin(String email, String password) {
        // DB에서 이메일로 사용자 조회
        UserDTO user = userMapper.findByEmail(email);
        
        // 사용자 존재 여부 및 삭제 상태 확인
        if (user == null || "Y".equals(user.getIsDeleted())) {
            return false;
        }
        
        // BCrypt를 사용하여 비밀번호 비교
        return passwordEncoder.matches(password, user.getPassword());
    }

    /* ======================================
       아이디 찾기
    ====================================== */
    // 8. 전화번호와 이름으로 사용자 조회
    @Override
    public UserDTO findByPhoneAndName(String phone, String name) {
        return userMapper.findByPhoneAndName(phone, name);
    }

    /* ======================================
       관리자 기능
    ====================================== */
    // 9. 권한 변경
    @Override
    public int updateUserRole(Long id, String role) {
        return userMapper.updateUserRole(id, role);
    }

    // 10. 회원 검색 (키워드/상태/역할 필터링)
    @Override
    public List<UserDTO> searchUsers(String keyword, String status, String role, int offset, int limit) {
        return userMapper.searchUsers(keyword, status, role, offset, limit);
    }
    
    // 11. 페이지네이션용 검색인원
    @Override
    public int countSearchUsers(String keyword, String status, String role) {
        return userMapper.countSearchUsers(keyword, status, role);
    }

    // 12. 역할별 유저 조회
    @Override
    public List<UserDTO> findByRole(String role) {
        return userMapper.findByRole(role);
    }
}