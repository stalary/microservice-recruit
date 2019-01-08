package com.stalary.pf.recruit.repo;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2018/4/14
 */
@Repository
public interface CompanyRepo extends BaseRepo<CompanyEntity, Long> {

    List<CompanyEntity> findByNameIsLike(String key);
}
