package org.embed.dto;

import java.time.LocalDateTime;
import org.embed.domain.Role;

import lombok.Data;

@Data
public class UserDTO {
	
	private Long id;
	private String email;
	private String password;
	private String name;
	private LocalDateTime birthDate;
	private String gender;
	private String phone;
	private String address;
	private String profileImageUrl;
	private String provider;
	private Role role;
	private String isDeleted;
	private LocalDateTime createdAt;

}
