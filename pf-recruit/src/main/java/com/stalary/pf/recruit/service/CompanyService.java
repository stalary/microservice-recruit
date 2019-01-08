package com.stalary.pf.recruit.service;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.CompanyAndRecruit;
import com.stalary.pf.recruit.repo.CompanyRepo;
import com.stalary.pf.recruit.repo.RecruitRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CompanyService
 *
 * @author lirongqian
 * @since 2018/04/14
 */
@Service
public class CompanyService extends BaseService<CompanyEntity, CompanyRepo> {

    CompanyService(CompanyRepo repo) {
        super(repo);
    }

    @Resource
    private RecruitRepo recruitRepo;

    public Map<String, Object> allCompany(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<CompanyEntity> companyList = repo.findAll(pageRequest);
        Map<String, Object> result = new HashMap<>();
        result.put("total", companyList.getTotalPages());
        result.put("companyList", companyList.getContent());
        return result;
    }

    public List<String> allCompanyName() {
        List<CompanyEntity> all = repo.findAll();
        return all.stream().map(CompanyEntity::getName).collect(Collectors.toList());
    }

    public List<CompanyEntity> findCompany(String key) {
        if (StringUtils.isEmpty(key)) {
            return repo.findAll();
        } else {
            return repo.findByNameIsLike("%" + key + "%");
        }
    }

    public CompanyAndRecruit getInfo(Long id) {
        CompanyEntity company = findOne(id);
        List<RecruitEntity> recruitList = recruitRepo.findByCompanyId(id);
        return new CompanyAndRecruit(company, recruitList);
    }

}