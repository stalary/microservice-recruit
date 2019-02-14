package com.stalary.pf.resume.repo;

import com.stalary.pf.resume.data.entity.Resume;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2018/4/13
 */
@Repository
public interface ResumeRepo extends BaseRepo<Resume, Long> {

    Resume findByUserId(long userId);

    List<Resume> findByUserIdIn(List<Long> userIdList);
}
