package com.stalary.pf.user.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.pf.user.client.UserCenterClient;
import com.stalary.pf.user.data.constant.Constant;
import com.stalary.pf.user.data.constant.RedisKeys;
import com.stalary.pf.user.data.dto.Applicant;
import com.stalary.pf.user.data.dto.HR;
import com.stalary.pf.user.data.dto.ProjectInfo;
import com.stalary.pf.user.data.dto.User;
import com.stalary.pf.user.data.vo.ResponseMessage;
import com.stalary.pf.user.exception.MyException;
import com.stalary.pf.user.exception.ResultEnum;
import com.stalary.pf.user.holder.UserHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * WebClient
 *
 * @author lirongqian
 * @since 2018/04/09
 */
@Slf4j
@Service
public class ClientService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;

    @Resource
    private UserCenterClient userCenterClient;

    @Value("${server.debug}")
    @Getter
    private boolean debug;

    private ProjectInfo projectInfo;

    public void genProjectInfo() {
        // 将项目信息存入缓存中
        if (StringUtils.isEmpty(redis.opsForValue().get(RedisKeys.PROJECT_INFO))) {
            ResponseMessage<ProjectInfo> info = userCenterClient.getProjectInfo("leader直聘", "17853149599");
            if (info.isSuccess()) {
                redis.opsForValue().set(RedisKeys.PROJECT_INFO, JSONObject.toJSONString(info.getData()));
                projectInfo = info.getData();
            } else {
                log.warn("genProjectInfo error");
            }
        } else {
            // 存在缓存时直接取出
            projectInfo = JSONObject.parseObject(redis.opsForValue().get(RedisKeys.PROJECT_INFO), ProjectInfo.class);
        }
    }

    public ResponseMessage postUser(Object object, String type) {
        User user;
        // 当登录注册时，判断对象类型
        if (object instanceof Applicant) {
            Applicant applicant = (Applicant) object;
            user = new User(applicant);
        } else if (object instanceof HR) {
            HR hr = (HR) object;
            user = new User(hr);
        } else {
            user = (User) object;
        }
        // 写入项目id，用于请求用户中心
        user.setProjectId(projectInfo.getProjectId());
        ResponseMessage<String> response = new ResponseMessage<>();
        if (Constant.UPDATE_PASSWORD.equals(type)) {
            response = userCenterClient.updatePassword(projectInfo.getKey(), user);
        } else if (Constant.UPDATE.equals(type)) {
            response = userCenterClient.updateInfo(projectInfo.getKey(), user);
        } else if (Constant.REGISTER.equals(type)) {
            // 注册需要先校验短信验证码
            String code = redis.opsForValue().get(Constant.getKey(RedisKeys.PHONE_CODE, user.getPhone()));
            if (StringUtils.isEmpty(code)) {
                throw new MyException(ResultEnum.CODE_EXPIRE);
            }
            if (!user.getCode().equals(code)) {
                throw new MyException(ResultEnum.CODE_ERROR);
            }
            response = userCenterClient.register(projectInfo.getKey(), user);
        } else if (Constant.LOGIN.equals(type)) {
            response = userCenterClient.login(projectInfo.getKey(), user);
        } else {
            log.warn("postUser type " + type + " error");
        }
        if (response.isSuccess()) {
            // 当修改密码后更新缓存
            redis.opsForValue().set(Constant.getKey(RedisKeys.USER_TOKEN, response.getData()), JSONObject.toJSONString(user));
        } else {
            throw new MyException(response.getCode(), response.getMsg());
        }
        return response;
    }

    /**
     * 获取到用户信息时即存入缓存
     *
     * @param token
     * @return
     */
    public User getUser(String token) {
        String redisKey = Constant.getKey(RedisKeys.USER_TOKEN, token);
        if (StringUtils.isEmpty(redis.opsForValue().get(redisKey))) {
            ResponseMessage<User> response = userCenterClient.getUserInfo(token, projectInfo.getKey());
            if (response.isSuccess()) {
                User user = response.getData();
                redis.opsForValue().set(redisKey, JSONObject.toJSONString(user));
                UserHolder.set(user);
                return user;
            } else {
                throw new MyException(response.getCode(), response.getMsg());
            }
        } else {
            User user = JSONObject.parseObject(redis.opsForValue().get(redisKey), User.class);
            UserHolder.set(user);
            return user;
        }
    }

    /**
     * 通过id获取用户信息
     */
    public User getUser(Long userId) {
        if (projectInfo == null) {
            genProjectInfo();
        }
        ResponseMessage<User> response = userCenterClient.getUserInfoById(userId, projectInfo.getKey(), projectInfo.getProjectId());
        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new MyException(response.getCode(), response.getMsg());
        }
    }

}