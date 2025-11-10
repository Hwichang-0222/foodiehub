package org.embed.interceptor;

import org.embed.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GlobalAuthInterceptor implements HandlerInterceptor {

	/* ========================================== */
	/*        전역 권한 검증 Interceptor          */
	/* ========================================== */

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");

		if (requestURI.startsWith("/admin")) {
			if (user == null) {
				response.sendRedirect("/user/login");
				return false;
			}

			if (!"ROLE_ADMIN".equals(user.getRole())) {
				throw new AccessDeniedException("관리자 권한이 필요합니다");
			}
		}

		if (requestURI.contains("/user/mypage") ||
			requestURI.contains("/user/edit") ||
			requestURI.contains("/user/update")) {
			if (user == null) {
				response.sendRedirect("/user/login");
				return false;
			}
		}

		if (requestURI.contains("/review/write") ||
			requestURI.contains("/review/reply")) {
			if (user == null) {
				response.sendRedirect("/user/login");
				return false;
			}
		}

		if (requestURI.contains("/board/create") ||
			requestURI.contains("/board/edit") ||
			requestURI.contains("/board/delete") ||
			requestURI.contains("/board/save")) {
			if (user == null) {
				response.sendRedirect("/user/login");
				return false;
			}
		}

		if (requestURI.contains("/restaurant/edit")) {
			if (user == null) {
				response.sendRedirect("/user/login");
				return false;
			}

			if (!"ROLE_ADMIN".equals(user.getRole()) && !"ROLE_OWNER".equals(user.getRole())) {
				throw new AccessDeniedException("식당 수정 권한이 없습니다");
			}
		}

		if (requestURI.contains("/restaurant/add")) {
			if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
				throw new AccessDeniedException("관리자 권한이 필요합니다");
			}
		}

		if (requestURI.contains("/restaurant/delete")) {
			if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
				throw new AccessDeniedException("관리자 권한이 필요합니다");
			}
		}

		return true;
	}
}
