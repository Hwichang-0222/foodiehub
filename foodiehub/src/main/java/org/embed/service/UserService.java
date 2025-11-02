package org.embed.service;

import java.util.List;

import org.embed.dto.UserDTO;

public interface UserService {

    /* ======================================
       기본 CRUD
    ====================================== */
    // 1. 개별 유저 조회 (id 기준)
    UserDTO findById(Long id);

    // 2. 이메일로 조회
    UserDTO findByEmail(String email);

    // 3. 새 유저 등록
    int insertUser(UserDTO user);

    // 4. 유저 정보 수정
    int updateUser(UserDTO user);

    // 5. 논리 삭제 (is_deleted = 'Y')
    int softDeleteUser(Long id);

    /* ======================================
       인증 관련
    ====================================== */
    // 6. 이메일 중복 확인
    int countByEmail(String email);

    // 7. 로그인 검증
    boolean validateLogin(String email, String password);

    /* ======================================
       아이디 찾기
    ====================================== */
    // 8. 전화번호와 이름으로 사용자 조회
    UserDTO findByPhoneAndName(String phone, String name);

    /* ======================================
       관리자 기능
    ====================================== */
    // 9. 권한 변경
    int updateUserRole(Long id, String role);

    // 10. 회원 검색 (키워드/상태/역할 필터링)
    List<UserDTO> searchUsers(String keyword, String status, String role, int offset, int limit);
    
    // 11. 페이지네이션용 검색인원
    int countSearchUsers(String keyword, String status, String role);

    // 12. 역할별 유저 조회
    List<UserDTO> findByRole(String role);

}