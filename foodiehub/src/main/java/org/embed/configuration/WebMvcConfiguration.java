package org.embed.configuration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    	String uploadPath = System.getProperty("user.dir") + "/uploads/";

        // '/uploads/**' 요청을 실제 파일 시스템의 '/uploads/' 로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600); // 1시간 캐싱
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // "yyyy-MM-dd" → LocalDate 변환기
        registry.addConverter(String.class, LocalDate.class, source -> {
            if (source == null || source.isEmpty()) return null;
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        });

        // "yyyy-MM-dd" → LocalDateTime 변환기
        registry.addConverter(String.class, LocalDateTime.class, source -> {
            if (source == null || source.isEmpty()) return null;
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        });
    }
}
