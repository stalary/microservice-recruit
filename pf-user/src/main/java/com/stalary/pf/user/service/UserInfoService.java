/**
 * @(#)UserInfoService.java, 2019-01-01.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.user.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.pf.user.client.RecruitClient;
import com.stalary.pf.user.data.constant.Constant;
import com.stalary.pf.user.data.constant.RedisKeys;
import com.stalary.pf.user.data.dto.CompanyAndJob;
import com.stalary.pf.user.data.dto.RecommendUser;
import com.stalary.pf.user.data.dto.Recruit;
import com.stalary.pf.user.data.dto.UploadAvatar;
import com.stalary.pf.user.data.entity.UserEs;
import com.stalary.pf.user.data.entity.UserInfoEntity;
import com.stalary.pf.user.data.vo.ReceiveInfo;
import com.stalary.pf.user.data.vo.ReceiveResume;
import com.stalary.pf.user.data.vo.SendInfo;
import com.stalary.pf.user.data.vo.SendResume;
import com.stalary.pf.user.holder.UserHolder;
import com.stalary.pf.user.repo.UserEsRepo;
import com.stalary.pf.user.repo.UserInfoRepo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * UserInfoService
 *
 * @author lirongqian
 * @since 2019/01/01
 */
@Service
public class UserInfoService {

    @Resource(name = "userInfoRepo")
    private UserInfoRepo repo;

    @Resource(name = "userEsRepo")
    private UserEsRepo esRepo;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;

    @Resource
    private RecruitClient recruitClient;

    public boolean uploadAvatar(UploadAvatar uploadAvatar) {
        UserInfoEntity entity = repo.findByUserId(uploadAvatar.getUserId());
        if (entity == null) {
            return false;
        }
        entity.setAvatar(uploadAvatar.getAvatar());
        repo.save(entity);
        return true;
    }

    public UserInfoEntity getInfo() {
        return repo.findByUserId(UserHolder.get().getId());
    }

    public UserInfoEntity getInfoByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public UserInfoEntity save(UserInfoEntity entity) {
        // 先存入es再存入mysql中
        esRepo.save(new UserEs(entity));
        return repo.save(entity);
    }

    public void saveEs(UserEs userEs) {
        esRepo.save(userEs);
    }

    /**
     * 获取投递简历列表
     */
    public SendInfo getSendList() {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        Long userId = UserHolder.get().getId();
        String sendKey = Constant.getKey(RedisKeys.RESUME_SEND, String.valueOf(userId));
        List<SendResume> ret = new ArrayList<>();
        Map<String, String> entries = redisHash.entries(sendKey);
        entries.forEach((k, v) -> ret.add(JSONObject.parseObject(v, SendResume.class)));
        return new SendInfo(ret);
    }

    /**
     * 获取收到的简历列表
     **/
    public ReceiveInfo getReceiveList() {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        // 多个岗位需要叠加
        Long userId = UserHolder.get().getId();
        List<Recruit> recruitList = recruitClient.getRecruitList(userId).getData();
        List<ReceiveResume> ret = new ArrayList<>();
        recruitList.forEach(recruit -> {
            String receiveKey = Constant.getKey(RedisKeys.RESUME_RECEIVE, String.valueOf(recruit.getId()));
            Map<String, String> entries = redisHash.entries(receiveKey);
            entries.forEach((k, v) -> ret.add(JSONObject.parseObject(v, ReceiveResume.class)));
        });
        ret.sort(Comparator.comparing(ReceiveResume::getRate).reversed());
        return new ReceiveInfo(ret);
    }

    public List<RecommendUser> getRecommendUser(List<CompanyAndJob> list) {
        List<RecommendUser> ret = new ArrayList<>();
        list.forEach(c -> ret.add(new RecommendUser(c.getRecruitId(), esRepo.findByIntentionCompanyOrIntentionJob(c.getCompany(), c.getJob()))));
        return ret;
    }
}