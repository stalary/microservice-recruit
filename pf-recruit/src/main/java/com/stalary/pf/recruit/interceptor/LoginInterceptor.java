package com.stalary.pf.recruit.interceptor;

import com.stalary.pf.recruit.annotation.LoginRequired;
import com.stalary.pf.recruit.exception.MyException;
import com.stalary.pf.recruit.exception.ResultEnum;
import com.stalary.pf.recruit.holder.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        // 判断需要调用需要登陆的接口时是否已经登陆
        boolean isLoginRequired = isAnnotationPresent(method, LoginRequired.class);
        if (isLoginRequired) {
            String userId = request.getHeader("userId");
            if (StringUtils.isEmpty(userId)) {
                throw new MyException(ResultEnum.NEED_LOGIN);
            }
            UserHolder.set(Long.valueOf(userId));
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

}