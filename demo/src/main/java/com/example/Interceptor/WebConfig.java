package com.example.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AddHeader())
                .addPathPatterns("/**");
        registry.addInterceptor(new AdminHead())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/login/json")
                .excludePathPatterns("/admin/media/music/update");
        registry.addInterceptor(new UserTypeisAdmin())
                .addPathPatterns("/admin/users/**")
                .addPathPatterns("/comment/delete/**")
                .addPathPatterns("/comment/delete")
                .addPathPatterns("/admin/records")
                .addPathPatterns("/admin/media")
                .excludePathPatterns("/admin/media/music/update");
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/templates/");
        super.addResourceHandlers(registry);
    }

}
