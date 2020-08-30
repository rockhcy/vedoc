package com.vesystem.version.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @auther hcy
 * @create 2020-08-26 14:26
 * @Description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private FrontInterceptor frontInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(frontInterceptor).addPathPatterns("/**").
                excludePathPatterns("/user/userLogin");

    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("POST","GET","DELETE","PUT");
    }

}
