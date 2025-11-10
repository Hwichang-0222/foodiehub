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
	@Override
	public UserDTO findById(Long id) {
		return userMapper.findById(id);
	}

	@Override
	public UserDTO findByEmail(String email) {
		return userMapper.findByEmail(email);
	}

	@Override
	public int insertUser(UserDTO user) {
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		}
		return userMapper.insertUser(user);
	}

	@Override
	public int updateUser(UserDTO user) {
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
		}
		return userMapper.updateUser(user);
	}

	@Override
	public int softDeleteUser(Long id) {
		return userMapper.softDeleteUser(id);
	}

	/* ======================================
	   인증 관련
	====================================== */
	@Override
	public int countByEmail(String email) {
		return userMapper.countByEmail(email);
	}

	@Override
	public boolean validateLogin(String email, String password) {
		UserDTO user = userMapper.findByEmail(email);

		if (user == null || "Y".equals(user.getIsDeleted())) {
			return false;
		}

		return passwordEncoder.matches(password, user.getPassword());
	}

	/* ======================================
	   아이디 찾기
	====================================== */
	@Override
	public UserDTO findByPhoneAndName(String phone, String name) {
		return userMapper.findByPhoneAndName(phone, name);
	}

	/* ======================================
	   관리자 기능
	====================================== */
	@Override
	public int updateUserRole(Long id, String role) {
		return userMapper.updateUserRole(id, role);
	}

	@Override
	public List<UserDTO> searchUsers(String keyword, String status, String role, int offset, int limit) {
		return userMapper.searchUsers(keyword, status, role, offset, limit);
	}

	@Override
	public int countSearchUsers(String keyword, String status, String role) {
		return userMapper.countSearchUsers(keyword, status, role);
	}

	@Override
	public List<UserDTO> findByRole(String role) {
		return userMapper.findByRole(role);
	}
}
