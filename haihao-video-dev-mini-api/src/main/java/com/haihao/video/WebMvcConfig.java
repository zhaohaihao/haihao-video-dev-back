package com.haihao.video;

import com.haihao.video.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by zhh on 2018/7/12 0012.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:C:/Users/Administrator/Desktop/haihao-video/");
    }

    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                        .addPathPatterns("/video/upload", "/video/uploadCover")
                                                    .addPathPatterns("/bgm/**");
        super.addInterceptors(registry);
    }
}
