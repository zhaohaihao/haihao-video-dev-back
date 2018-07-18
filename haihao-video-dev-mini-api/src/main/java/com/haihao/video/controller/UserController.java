package com.haihao.video.controller;

import com.haihao.video.pojo.Users;
import com.haihao.video.pojo.vo.UsersVO;
import com.haihao.video.service.UserService;
import com.haihao.video.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by zhh on 2018/7/10 0010.
 */
@RestController
@Api(value = "用户相关业务的接口", tags = {"用户相关业务的 Controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile file) throws Exception {

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空!");
        }

        // 文件保存的命名空间
        String fileSpace = "C:/Users/Administrator/Desktop/haihao-video";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {
                String filename = file.getOriginalFilename();
                if (!StringUtils.isEmpty(filename)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + filename;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + filename);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("头像图片上传异常!");
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);

        return IMoocJSONResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/queryUserInfo")
    public IMoocJSONResult queryUserInfo(String userId) throws Exception {
        if (StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空!");
        }

        Users users = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        return IMoocJSONResult.ok(usersVO);
    }
}
