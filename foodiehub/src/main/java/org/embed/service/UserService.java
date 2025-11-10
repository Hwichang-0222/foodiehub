package org.embed.service;

import java.util.List;

import org.embed.dto.UserDTO;

public interface UserService {

	/* ========================================== */
	/*             기본 CRUD                      */
	/* ========================================== */
	UserDTO findById(Long id);

	UserDTO findByEmail(String email);

	int insertUser(UserDTO user);

	int updateUser(UserDTO user);

	int softDeleteUser(Long id);

	/* ========================================== */
	/*             인증 관련                      */
	/* ========================================== */
	int countByEmail(String email);

	boolean validateLogin(String email, String password);

	/* ========================================== */
	/*           아이디 찾기                      */
	/* ========================================== */
	UserDTO findByPhoneAndName(String phone, String name);

	/* ========================================== */
	/*           관리자 기능                      */
	/* ========================================== */
	int updateUserRole(Long id, String role);

	List<UserDTO> searchUsers(String keyword, String status, String role, int offset, int limit);

	int countSearchUsers(String keyword, String status, String role);

	List<UserDTO> findByRole(String role);

}
