package org.embed.dto;

import java.time.LocalDateTime;

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
	private String regionLevel1;
	private String regionLevel2;
	private String regionLevel3;
	private String profileImageUrl;
	private String provider;
	private String role;
	private String isDeleted;
	private LocalDateTime createdAt;

}
