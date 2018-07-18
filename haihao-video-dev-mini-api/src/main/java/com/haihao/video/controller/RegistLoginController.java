package com.haihao.video.controller;

import com.haihao.video.pojo.Users;
import com.haihao.video.pojo.vo.UsersVO;
import com.haihao.video.service.UserService;
import com.haihao.video.utils.IMoocJSONResult;
import com.haihao.video.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhh on 2018/7/10 0010.
 */
@RestController
@Api(value = "用户注册登录的接口", tags = {"注册和登录的 Controller"})
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users users) throws Exception {

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmpty(users.getUsername()) || StringUtils.isEmpty(users.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空!");
        }

        // 2.判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(users.getUsername());

        // 3.保存用户, 注册信息
        if (!usernameIsExist) {
            users.setNickname(users.getUsername());
            users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
            users.setFansCounts(0);
            users.setReceiveLikeCounts(0);
            users.setFollowCounts(0);
            userService.saveUser(users);
        } else {
            return IMoocJSONResult.errorMsg("用户名已经存在!");
        }

        users.setPassword("");
        UsersVO usersVO = setUserRedisSessionToken(users);

        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users) throws Exception {
        String username = users.getUsername();
        String password = users.getPassword();

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空!");
        }

        // 2.判断用户是否存在
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        // 3.返回
        if (userResult != null) {
            userResult.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(userResult);
            return IMoocJSONResult.ok(usersVO);
        } else {
            return IMoocJSONResult.errorMsg("用户名或者密码不正确, 请重试!");
        }
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }
}
