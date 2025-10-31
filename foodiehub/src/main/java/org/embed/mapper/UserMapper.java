package org.embed.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.UserDTO;

@Mapper
public interface UserMapper {

    /* ------------------------------
       기본 CRUD
    ------------------------------ */
    // 개별 유저 조회 (id 기준)
    UserDTO findById(@Param("id") Long id);

    // 이메일로 조회
    UserDTO findByEmail(@Param("email") String email);

    // 새 유저 등록
    int insertUser(UserDTO user);

    // 유저 정보 수정
    int updateUser(UserDTO user);

    // 논리 삭제 (is_deleted = 'Y')
    int softDeleteUser(@Param("id") Long id);

    /* ------------------------------
       인증 관련
    ------------------------------ */
    // 이메일 중복 확인
    int countByEmail(@Param("email") String email);

    // 로그인 검증 (이메일+비밀번호)
    UserDTO validateLogin(@Param("email") String email, @Param("password") String password);

    /* ------------------------------
       관리자 기능
    ------------------------------ */
    // 권한 변경
    int updateUserRole(@Param("id") Long id, @Param("role") String role);

    // 회원 검색 (키워드/상태/역할 필터링)
    List<UserDTO> searchUsers(@Param("keyword") String keyword,
                               @Param("status") String status,
                               @Param("role") String role,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int countSearchUsers(@Param("keyword") String keyword,
                         @Param("status") String status,
                         @Param("role") String role);

    // 역할별 유저 조회
    List<UserDTO> findByRole(@Param("role") String role);

}