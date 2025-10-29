package org.embed.service;

import java.util.List;

import org.embed.dto.UserDTO;

public interface UserService {

    // 1. 전체 유저 조회 (삭제되지 않은 유저만)
    List<UserDTO> findAllUsers(int page, int limit);

    // 2. 개별 유저 조회 (id 기준)
    UserDTO findById(Long id);

    // 3. 이메일로 조회 (로그인용)
    UserDTO findByEmail(String email);

    // 4. 새 유저 등록
    int insertUser(UserDTO user);

    // 5. 유저 정보 수정
    int updateUser(UserDTO user);

    // 6. 논리 삭제 (is_deleted = 'Y')
    int softDeleteUser(Long id);

    // 7. 관리자 전용: 유저권한(Role) 변경
    int updateUserRole(Long id, String role);
    
    // 8. 이메일 중복 확인용
    public int countByEmail(String email);
    
    // 9. 로그인 검증용
    public boolean validateLogin(String email, String password);

    // 10. 권한별 유저 조회 (관리자 전용)
    public List<UserDTO> getUsersByRole(String role);
    
    // 11. 회원 검색 (관리자용)
    List<UserDTO> searchUsers(String keyword, String status, String role, int offset, int limit);
    int countSearchUsers(String keyword, String status, String role);

    
    // 12. 역활 기준 검색 
    List<UserDTO> findByRole(String role);
    
    int countAllUsers();

    
}
