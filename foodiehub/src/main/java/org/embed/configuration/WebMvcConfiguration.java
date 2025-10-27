package org.embed.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 실제 파일 경로
        String uploadPath = System.getProperty("user.dir") + "/uploads/";

        // '/uploads/**' 요청을 실제 파일 시스템의 '/uploads/' 로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600); // 1시간 캐싱
    }
}
