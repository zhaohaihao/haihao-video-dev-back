package com.haihao.video;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by zhh on 2018/7/11 0011.
 */
@Configuration // 配置
@EnableSwagger2 // 开启配置
public class Swagger2 {

    /**
     * swagger2 的配置文件, 这里可以配置 swagger2 的一些基本的内容, 比如扫描的包等等
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.haihao.video.controller"))
                .paths(PathSelectors.any()).build();
    }

    /**
     * 构建 api 文档的信息
     * @return
     */
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面的标题
                .title("使用 swagger2 构建短视频后端 api 接口文档")
                // 设置联系人
                .contact(new Contact("zhaohaohai", "http://www.seebigsea.com", "bigsea1994@gmail.com"))
                // 描述
                .description("欢迎访问短视频结构文档, 这里是描述信息")
                // 定义版本号
                .version("1.0").build();

    }
}
