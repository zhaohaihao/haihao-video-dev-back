package com.haihao.video.service;

import com.haihao.video.pojo.Users;

/**
 * Created by zhh on 2018/7/11 0011.
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 保存用户(用户注册)
     * @param users
     */
    void saveUser(Users users);

    /**
     * 用户登录, 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    Users queryUserForLogin(String username, String password);

    /**
     * 用户修改信息
     * @param users
     */
    void updateUserInfo(Users users);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Users queryUserInfo(String userId);
}
