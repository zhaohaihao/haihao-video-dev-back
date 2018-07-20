package com.haihao.video.controller;

import com.haihao.video.pojo.Users;
import com.haihao.video.pojo.vo.UsersVO;
import com.haihao.video.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by zhh on 2018/7/10 0010.
 */
@RestController
public class BasicController {

    @Autowired
    protected RedisOperator redis;
    
    public static final String USER_REDIS_SESSION = "user-redis-session";

    // 文件保存的命名空间
    public static final String FILE_SPACE = "C:/Users/Administrator/Desktop/haihao-video";

    // ffmpeg所在目录
    public static final String FFMPEG_EXE = "F:\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe";

    // 页面大小
    public static final Integer PAGE_SIZE = 5;

    /***
     * 生成无状态session
     * @param userModel
     */
    protected UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel, usersVO);
        usersVO.setUserToken(uniqueToken);

        return usersVO;
    }
}
