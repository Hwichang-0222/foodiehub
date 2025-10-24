package org.embed.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.UserDTO;

@Mapper
public interface UserMapper {
	
	// 1. 전체 유저 조회 (삭제 안 된 사용자만)
	List<UserDTO> findAllActiveUsers();

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
	int updateUserRole(@Param("id") Long id, @Param("role") String role);
	
	// 8. 이메일 중복 확인용
	int countByEmail(String email);

	// 9. 로그인 검증용
	UserDTO validateLogin(@Param("email") String email, @Param("password") String password);

	// 10. 권한별 유저 조회 (관리자 전용)
	List<UserDTO> findUsersByRole(String role);
}
