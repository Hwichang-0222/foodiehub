package org.embed.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.UserDTO;

@Mapper
public interface UserMapper {

	/* ========================================== */
	/*             기본 CRUD                      */
	/* ========================================== */
	UserDTO findById(@Param("id") Long id);

	UserDTO findByEmail(@Param("email") String email);

	int insertUser(UserDTO user);

	int updateUser(UserDTO user);

	int softDeleteUser(@Param("id") Long id);

	/* ========================================== */
	/*             인증 관련                      */
	/* ========================================== */
	int countByEmail(@Param("email") String email);

	UserDTO validateLogin(@Param("email") String email, @Param("password") String password);

	/* ========================================== */
	/*           아이디 찾기                      */
	/* ========================================== */
	UserDTO findByPhoneAndName(@Param("phone") String phone, @Param("name") String name);

	/* ========================================== */
	/*           관리자 기능                      */
	/* ========================================== */
	int updateUserRole(@Param("id") Long id, @Param("role") String role);

	List<UserDTO> searchUsers(@Param("keyword") String keyword,
							   @Param("status") String status,
							   @Param("role") String role,
							   @Param("offset") int offset,
							   @Param("limit") int limit);

	int countSearchUsers(@Param("keyword") String keyword,
						 @Param("status") String status,
						 @Param("role") String role);

	List<UserDTO> findByRole(@Param("role") String role);

}
