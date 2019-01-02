/**
 * @(#)FacadeController.java, 2018-12-31.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.pf.outside.controller;

import com.stalary.pf.outside.annotation.LoginRequired;
import com.stalary.pf.outside.data.Email;
import com.stalary.pf.outside.data.ResponseMessage;
import com.stalary.pf.outside.service.MailService;
import com.stalary.pf.outside.service.SmsService;
import com.stalary.pf.outside.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * FacadeController
 *
 * @author lirongqian
 * @since 2018/12/31
 */
@RestController
@RequestMapping("/outside")
public class FacadeController {

    @Resource
    private SmsService smsService;

    @Resource
    private UserService userService;

    @Resource
    private MailService mailService;

    /**
     * @method code 发送短信验证码的接口
     * @param phone 手机号
     **/
    @GetMapping("/code")
    public ResponseMessage code(
            @RequestParam String phone) {
        String code = smsService.sendCode(phone);
        if (StringUtils.isNotEmpty(code)) {
            return ResponseMessage.successMessage("发送成功");
        } else {
            return ResponseMessage.failedMessage("发送失败");
        }
    }

    /**
     * @method upload 上传用户头像
     * @param avatar 头像
     **/
    @PostMapping("/avatar")
    @LoginRequired
    public ResponseMessage upload(
            @RequestParam("avatar") MultipartFile avatar) {
        boolean flag = userService.uploadAvatar(avatar);
        if (flag) {
            return ResponseMessage.successMessage("头像上传成功");
        } else {
            return ResponseMessage.failedMessage("上传头像失败");
        }
    }

    @PostMapping("/email")
    public ResponseMessage sendEmail(
            @RequestBody Email email) {
        mailService.sendEmail(email.getEmail(), email.getTitle(), email.getContent());
        return ResponseMessage.successMessage("邮件发送成功");
    }
}