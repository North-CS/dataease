package io.dataease.auth.interceptor;

import io.dataease.constant.AuthConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Value("#{'${dataease.origin-list:http://127.0.0.1:8100}'.split(',')}")
    private List<String> originList;

    private CorsRegistration operateCorsRegistration;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(AuthConstant.DE_API_PREFIX, c -> c.isAnnotationPresent(RestController.class) && c.getPackageName().startsWith("io.dataease"));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        operateCorsRegistration = registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(originList.toArray(new String[0]))
                .allowedHeaders("*")
                .maxAge(3600)
                .allowedMethods("GET", "POST");
    }

    public void addAllowedOrigins(List<String> origins) {
        if (CollectionUtils.isEmpty(origins)) {
            return;
        }
        origins.addAll(originList);
        List<String> newOrigins = origins.stream().distinct().toList();
        String[] originArray = newOrigins.toArray(new String[0]);
        operateCorsRegistration.allowedOrigins(originArray);
    }
}
