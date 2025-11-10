package org.embed.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecureConfiguration {

	/* ========================================== */
	/*           비밀번호 암호화                  */
	/* ========================================== */

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/* ========================================== */
	/*      Security FilterChain 설정            */
	/* ========================================== */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())

			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/css/**", "/js/**", "/images/**","/debug/auth").permitAll()
				.requestMatchers("/error", "/error/**").permitAll()

				.requestMatchers("/user/signup", "/user/login", "/user/logout", "/user/check-email",
								"/user/find-id", "/user/find-password").permitAll()

				.requestMatchers("/restaurant/list", "/restaurant/detail/**").permitAll()

				.requestMatchers("/restaurant/add").hasAuthority("ROLE_ADMIN")

				.requestMatchers("/restaurant/edit/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")

				.requestMatchers("/restaurant/delete/**").hasAuthority("ROLE_ADMIN")

				.requestMatchers("/review/write", "/review/reply").authenticated()

				.requestMatchers("/board/list", "/board/search").permitAll()

				.requestMatchers("/board/detail/**").permitAll()

				.requestMatchers("/board/create", "/board/save").authenticated()

				.requestMatchers("/board/edit/**").authenticated()

				.requestMatchers("/board/delete/**").authenticated()

				.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

				.anyRequest().authenticated()
			)

			.formLogin(login -> login.disable())

			.logout(logout -> logout.disable());

		return http.build();
	}
}
