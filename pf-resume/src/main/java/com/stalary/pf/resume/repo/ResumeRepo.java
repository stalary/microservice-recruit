package com.stalary.pf.resume.repo;

import com.stalary.pf.resume.data.entity.ResumeEntity;
import org.springframework.stereotype.Repository;

/**
 * @author Stalary
 * @description
 * @date 2018/4/13
 */
@Repository
public interface ResumeRepo extends BaseRepo<ResumeEntity, Long> {

    ResumeEntity findByUserId(long userId);
}
