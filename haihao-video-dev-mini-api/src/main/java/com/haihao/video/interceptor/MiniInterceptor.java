package com.haihao.video.interceptor;

import com.haihao.video.controller.BasicController;
import com.haihao.video.utils.IMoocJSONResult;
import com.haihao.video.utils.JsonUtils;
import com.haihao.video.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by zhh on 2018/7/20 0020.
 */
public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    protected RedisOperator redis;

    /**
     * 拦截请求, 在 controller 调用之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return 返回 false: 请求被拦截, 返回；返回 true: 请求通过
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 获取验证信息
        String userId = httpServletRequest.getHeader("userId");
        String userToken = httpServletRequest.getHeader("userToken");

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
            returnErrorResponse(httpServletResponse, new IMoocJSONResult().errorTokenMsg("请登录..."));
            System.out.println("请登录...");
            return false;
        }

        String uniqueToken = redis.get(BasicController.USER_REDIS_SESSION + ":" + userId);
        if (StringUtils.isEmpty(uniqueToken)) {
            returnErrorResponse(httpServletResponse, new IMoocJSONResult().errorTokenMsg("请登录..."));
            System.out.println("请登录...");
            return false;
        }

        if (!uniqueToken.equals(userToken)) {
            returnErrorResponse(httpServletResponse, new IMoocJSONResult().errorTokenMsg("账号被挤出..."));
            System.out.println("该账号已经在别的手机端登录...");
            return false;
        }
        return true;
    }

    /**
     * 请求 controller 之后, 渲染视图之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求 controller 之后, 视图渲染之后
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse httpServletResponse, IMoocJSONResult result) throws Exception {
        OutputStream outputStream = null;
        try {
            httpServletResponse.setCharacterEncoding("utf-8");
            httpServletResponse.setContentType("text/json");
            outputStream = httpServletResponse.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("UTF-8"));
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }
}
