package com.stalary.pf.user.interceptor;

import com.stalary.pf.user.annotation.LoginRequired;
import com.stalary.pf.user.data.constant.Constant;
import com.stalary.pf.user.data.constant.RedisKeys;
import com.stalary.pf.user.data.dto.User;
import com.stalary.pf.user.exception.MyException;
import com.stalary.pf.user.exception.ResultEnum;
import com.stalary.pf.user.holder.UserHolder;
import com.stalary.pf.user.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * LoginInterceptor
 *
 * @author lirongqian
 * @since 2018/04/09
 */
@Slf4j
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private ClientService clientService;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        clientService.genProjectInfo();
        // 判断需要调用需要登陆的接口时是否已经登陆
        boolean isLoginRequired = isAnnotationPresent(method, LoginRequired.class);
        if (isLoginRequired) {
            String uri = request.getRequestURI();
            String token = getToken(getAuthHeader(request));
            User user = clientService.getUser(token);
            if (user == null) {
                // token无法获取到用户信息代表未登陆
                throw new MyException(ResultEnum.NEED_LOGIN);
            }
            // 退出时删除缓存
            if (uri.contains(Constant.LOGOUT)) {
                redis.delete(Constant.getKey(RedisKeys.USER_TOKEN, token));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除ThreadLocal
        UserHolder.remove();
    }

    private boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
        // 查找类注解或者方法注解
        return method.getDeclaringClass().isAnnotationPresent(annotationClass) || method.isAnnotationPresent(annotationClass);
    }

    /**
     * 获取token
     */
    private String getAuthHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(Constant.Authorization);
        log.info("authHeader " + authHeader);
        return authHeader;
    }

    private String getToken(String authHeader) {
        String token = null;
        if (StringUtils.isNotEmpty(authHeader)) {
            token = authHeader.split(" ")[1];
        } else {
            if (clientService.isDebug()) {
                token = "2050dbc3ef8ccc5ac10c1e8b85c5b1ee9113424a80530f7717e0e3c2a7bc99b1";
            }
        }
        return token;
    }
}