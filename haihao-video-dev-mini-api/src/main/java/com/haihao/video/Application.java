package com.haihao.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by zhh on 2018/7/10 0010.
 */
@SpringBootApplication
@MapperScan(basePackages = "com.haihao.video.mapper")
@ComponentScan(basePackages = {"com.haihao.video", "org.n3r.idworker"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
