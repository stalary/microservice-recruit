package com.stalary.pf.recruit.repo;

import com.stalary.pf.recruit.data.entity.RecruitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stalary
 * @description
 * @date 2018/4/17
 */
@Repository
public interface RecruitRepo extends BaseRepo<RecruitEntity, Long> {

    List<RecruitEntity> findByTitleIsLike(String key);

    List<RecruitEntity> findByHrId(Long userId);

    List<RecruitEntity> findByCompanyId(Long id);

    List<RecruitEntity> findByTitleIn(List<String> titleList);
}
