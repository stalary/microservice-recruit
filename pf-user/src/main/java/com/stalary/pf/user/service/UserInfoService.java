/**
 * @(#)UserInfoService.java, 2019-01-01.
 *
 * Copyright 2019 Stalary.
 */
package com.stalary.pf.user.service;

import com.stalary.pf.user.data.dto.UploadAvatar;
import com.stalary.pf.user.data.entity.UserInfoEntity;
import com.stalary.pf.user.holder.UserHolder;
import com.stalary.pf.user.repo.UserInfoRepo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    public UserInfoEntity save(UserInfoEntity entity) {
        return repo.save(entity);
    }
}