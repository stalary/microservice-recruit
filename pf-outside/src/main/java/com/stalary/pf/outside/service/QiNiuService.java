package com.stalary.pf.outside.service;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.stalary.pf.outside.exception.MyException;
import com.stalary.pf.outside.exception.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * QiNiuService
 *
 * @author lirongqian
 * @since 2018/04/23
 */
@Service
@Slf4j
public class QiNiuService {

    /**
     * 七牛云
     **/
    private static String ACCESS_KEY = "zfg7aGCs98DbKp_zKHzOAwzd6BoPhLjPkO5ohzEG";
    private static String SECRET_KEY = "l-fs00VcPP2nZRBKZmJj7LeSShi2wKxSMN5RL10w";
    private static String BUCKET_NAME = "stalary";
    private static String QINIU_IMAGE_DOMAIN = "http://qiniu.stalary.com/";
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    Configuration cfg = new Configuration(Zone.huadong());
    private UploadManager uploadManager = new UploadManager(cfg);

    private String getUpToken() {
        return auth.uploadToken(BUCKET_NAME);
    }

    public String uploadPicture(MultipartFile picture, String name) {
        try {
            Response response = uploadManager.put(picture.getBytes(), name, getUpToken());
            if (response.isOK() && response.isJson()) {
                return QINIU_IMAGE_DOMAIN + name;
            }
        } catch (Exception e) {
            log.warn("upload picture error", e);
            throw new MyException(ResultEnum.QINIU_ERROR);
        }
        return QINIU_IMAGE_DOMAIN + name;
    }
}