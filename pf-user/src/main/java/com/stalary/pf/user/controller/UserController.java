package com.stalary.pf.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.stalary.pf.user.annotation.LoginRequired;
import com.stalary.pf.user.client.MessageClient;
import com.stalary.pf.user.data.constant.Constant;
import com.stalary.pf.user.data.dto.*;
import com.stalary.pf.user.data.entity.UserInfoEntity;
import com.stalary.pf.user.data.vo.LoginVo;
import com.stalary.pf.user.data.vo.ResponseMessage;
import com.stalary.pf.user.holder.IpHolder;
import com.stalary.pf.user.holder.UserHolder;
import com.stalary.pf.user.service.ClientService;
import com.stalary.pf.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * UserController
 * @description 用户相关接口
 * @author lirongqian
 * @since 2019/01/01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ClientService clientService;

    @Resource
    private MessageClient messageClient;

    /**
     * @method register 求职者注册
     * @param applicant 求职者对象
     * @return 0 token
     **/
    @PostMapping
    public ResponseMessage register(
            @RequestBody Applicant applicant) {
        return clientService.postUser(applicant, Constant.REGISTER);
    }

    /**
     * @method login 传入登陆对象，仅需要用户名和密码
     * @param user 用户对象
     * @return LoginVo 登陆对象
     **/
    @PostMapping("/login")
    public ResponseMessage login(@RequestBody User user) {
        ResponseMessage responseMessage = clientService.postUser(user, Constant.LOGIN);
        if (!responseMessage.isSuccess()) {
            return responseMessage;
        }
        String token = responseMessage.getData().toString();
        User getUser = clientService.getUser(token);
        LoginVo loginVo = new LoginVo(token, getUser.getRole(), getUser.getId(), getUser.getFirstId());
        messageClient.sendCount(getUser.getId());
        return ResponseMessage.successMessage(loginVo);
    }


    /**
     * @method hrRegister hr注册
     * @param hr hr对象
     * @return 0 token
     **/
    @PostMapping("/hr")
    public ResponseMessage hrRegister(
            @RequestBody HR hr) {
        return clientService.postUser(hr, Constant.REGISTER);
    }

    /**
     * @method logout 退出登录
     * @return 0 退出成功
     **/
    @GetMapping("/logout")
    @LoginRequired
    public ResponseMessage logout() {
        return ResponseMessage.successMessage("退出成功");
    }

    /**
     * @method getInfo header中带入token获取用户信息
     * @return UserInfo 用户信息
     **/
    @GetMapping
    @LoginRequired
    public ResponseMessage getInfo() {
        return ResponseMessage.successMessage(userInfoService.getInfo());
    }

    @GetMapping("/info")
    public ResponseMessage getInfoByUserId(
            @RequestParam Long userId) {
        return ResponseMessage.successMessage(userInfoService.getInfoByUserId(userId));
    }

    /**
     * @method updateInfo 修改用户信息
     * @param userInfo 用户信息对象
     * @return UserInfo 用户信息
     **/
    @PutMapping("/info")
    @LoginRequired
    public ResponseMessage updateInfo(
            @RequestBody UserInfoEntity userInfo) {
        User user = UserHolder.get();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        return ResponseMessage.successMessage(userInfoService.save(userInfo));
    }

    /**
     * @method updatePhone 修改手机号
     * @param params 手机号
     */
    @PutMapping("/phone")
    @LoginRequired
    public ResponseMessage updatePhone(
            @RequestBody Map<String, String> params) {
        User user = UserHolder.get();
        user.setPhone(params.get("phone"));
        return clientService.postUser(user, Constant.UPDATE);
    }

    /**
     * @method updatePassword 修改密码，通过新密码进行修改
     * @param params 新密码
     */
    @PutMapping("/password")
    @LoginRequired
    public ResponseMessage updatePassword(
            @RequestBody Map<String, String> params) {
        User user = UserHolder.get();
        user.setPassword(params.get("password"));
        return clientService.postUser(user, Constant.UPDATE_PASSWORD);
    }

    /**
     * @method forgetPassword 忘记密码，通过用户名，手机号，新密码进行修改
     * @param params 参数
     **/
    @PostMapping("/password")
    public ResponseMessage forgetPassword(
            @RequestBody Map<String, String> params) {
        User user = new User();
        user.setPassword(params.get("password"));
        user.setUsername(params.get("username"));
        user.setPhone(params.get("phone"));
        return clientService.postUser(user, Constant.UPDATE_PASSWORD);
    }

    /**
     * @method updateEmail 修改邮箱
     * @param params 邮箱
     */
    @PutMapping("/email")
    @LoginRequired
    public ResponseMessage updateEmail(
            @RequestBody Map<String, String> params) {
        User user = UserHolder.get();
        user.setEmail(params.get("email"));
        return clientService.postUser(user, Constant.UPDATE);
    }

    /**
     * @method token 使用token获取用户信息
     * @param token token
     * @return User 用户信息
     **/
    @GetMapping("/token")
    public ResponseMessage token(
            @RequestParam String token) {
        return ResponseMessage.successMessage(clientService.getUser(token));
    }

    /**
     * @method getSendList 查看个人投递列表
     * @return SendInfo 投递信息
     **/
    @GetMapping("/send")
    @LoginRequired
    public ResponseMessage getSendList() {
        return ResponseMessage.successMessage(userInfoService.getSendList());
    }

    /**
     * @method getReceiveList 查看获取的简历列表
     * @return ReceiveInfo 简历信息
     **/
    @GetMapping("/receive")
    @LoginRequired
    public ResponseMessage getReceiveList() {
        return ResponseMessage.successMessage(userInfoService.getReceiveList());
    }

    ///////////////////////// 内部服务使用的接口 //////////////////////////////////

    @PostMapping("/avatar")
    public ResponseMessage uploadAvatar(
            @RequestBody UploadAvatar uploadAvatar) {
        boolean flag = userInfoService.uploadAvatar(uploadAvatar);
        if (flag) {
            return ResponseMessage.successMessage("上传头像成功");
        } else {
            return ResponseMessage.failedMessage("上传头像失败");
        }
    }

    @GetMapping("/recommend")
    public ResponseMessage getRecommendUser(
            @RequestParam String param) {
        List<CompanyAndJob> companyAndJobList = JSONObject.parseObject(param, new TypeReference<List<CompanyAndJob>>() {
        });
        return ResponseMessage.successMessage(userInfoService.getRecommendUser(companyAndJobList));
    }

    @GetMapping("/email")
    public ResponseMessage getEmail(
            @RequestParam Long userId) {
        return ResponseMessage.successMessage(clientService.getUser(userId).getEmail());
    }

    @GetMapping("/userId")
    public ResponseMessage getUserById(@RequestParam Long userId) {
        return ResponseMessage.successMessage(clientService.getUser(userId));
    }

}