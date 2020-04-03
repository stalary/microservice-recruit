package com.stalary.pf.outside.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.stalary.pf.outside.data.Constant;
import com.stalary.pf.outside.exception.MyException;
import com.stalary.pf.outside.exception.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * SmsService
 *
 * @author lirongqian
 * @since 2018/04/23
 */
@Service
@Slf4j
public class SmsService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;

    /**
     * 阿里短信
     **/
    private static final String PRODUCT = "Dysmsapi";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "xxx";
    private static final String ACCESS_KEY_SECRET = "xxx";

    private IAcsClient getAcsClient() throws ClientException {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
        return new DefaultAcsClient(profile);
    }

    public String sendCode(String phone) {
        String randomCode = RandomStringUtils.randomNumeric(6);
        if (sendSms(phone, randomCode)) {
            // 有效期一天
            redis.opsForValue().set(Constant.getKey(Constant.PHONE_CODE, phone), randomCode, 1, TimeUnit.DAYS);
            return randomCode;
        }
        return null;
    }

    private boolean sendSms(String phoneNumber, String randomCode) {
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(phoneNumber);
        request.setSignName("leader直聘");
        request.setTemplateCode("SMS_130924380");
        request.setTemplateParam("{\"code\":\"" + randomCode + "\"}");
        try {
            SendSmsResponse sendSmsResponse = getAcsClient().getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals(Constant.OK)) {
                log.info("发送短信成功");
                return true;
            }
            log.error(sendSmsResponse.getCode());
        } catch (ClientException e) {
            log.error("ClientException异常：" + e.getMessage());
            throw new MyException(ResultEnum.SEND_NOTE_ERROR);
        }
        log.error("发送短信失败");
        return false;
    }
}
