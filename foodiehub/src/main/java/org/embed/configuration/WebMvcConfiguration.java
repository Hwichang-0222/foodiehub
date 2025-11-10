package org.embed.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String uploadPath = System.getProperty("user.dir") + "/uploads/";

		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:" + uploadPath)
				.setCachePeriod(3600);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(String.class, LocalDate.class, source -> {
			if (source == null || source.isEmpty()) return null;
			return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		});

		registry.addConverter(String.class, LocalDateTime.class, source -> {
			if (source == null || source.isEmpty()) return null;
			return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
		});
	}

	/* ========================================== */
	/*        전역 Interceptor 등록              */
	/* ========================================== */

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new org.embed.interceptor.GlobalAuthInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/", "/css/**", "/js/**", "/images/**", "/uploads/**",
									"/user/login", "/user/signup", "/user/logout",
									"/user/check-email", "/user/find-id", "/user/find-password",
									"/restaurant/list", "/restaurant/detail/**",
									"/board/list", "/board/search", "/board/detail/**",
									"/error");
	}
}
