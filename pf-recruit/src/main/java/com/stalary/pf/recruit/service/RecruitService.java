package com.stalary.pf.recruit.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.lightmqclient.facade.Producer;
import com.stalary.pf.recruit.client.UserClient;
import com.stalary.pf.recruit.data.constant.Constant;
import com.stalary.pf.recruit.data.dto.SendResume;
import com.stalary.pf.recruit.data.dto.User;
import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.HR;
import com.stalary.pf.recruit.data.vo.RecruitAndCompany;
import com.stalary.pf.recruit.data.vo.RecruitAndHrAndCompany;
import com.stalary.pf.recruit.repo.RecruitRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecruitService
 *
 * @author lirongqian
 * @since 2018/04/17
 */
@Service
@Slf4j
public class RecruitService extends BaseService<RecruitEntity, RecruitRepo> {

    RecruitService(RecruitRepo repo) {
        super(repo);
    }

    @Resource
    private UserClient userClient;

    @Resource
    private CompanyService companyService;

    @Resource
    private Producer producer;

    public Map<String, Object> allRecruit(String key, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<RecruitAndCompany> list = new ArrayList<>();
        List<RecruitEntity> recruitList;
        Page<RecruitEntity> recruitPage;
        if (StringUtils.isEmpty(key)) {
            recruitPage = repo.findAll(pageRequest);
            recruitList = recruitPage.getContent();
        } else {
            recruitPage = repo.findByTitleIsLike("%" + key + "%", pageRequest);
            recruitList = recruitPage.getContent();
        }
        recruitList.forEach(recruit -> {
            recruit.deserializeFields();
            list.add(new RecruitAndCompany(recruit, companyService.findOne(recruit.getCompanyId())));
        });
        Map<String, Object> result = new HashMap<>();
        result.put("total", recruitPage.getTotalPages());
        result.put("recruitList", list);
        return result;
    }

    public RecruitAndHrAndCompany getRecruitInfo(Long id) {
        RecruitEntity recruit = findById(id);
        User user = userClient.getUser(recruit.getHrId()).getData();
        HR hr = new HR().toBuilder()
                .companyId(recruit.getCompanyId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .username(user.getUsername()).build();
        CompanyEntity company = companyService.findOne(recruit.getCompanyId());
        return new RecruitAndHrAndCompany(recruit, hr, company);
    }

    public RecruitEntity saveRecruit(RecruitEntity recruit) {
        recruit.serializeFields();
        RecruitEntity save = repo.save(recruit);
        save.deserializeFields();
        return save;
    }

    public List<RecruitEntity> findByUserId(Long userId) {
        List<RecruitEntity> recruitList = repo.findByHrId(userId);
        recruitList.forEach(RecruitEntity::deserializeFields);
        return recruitList;
    }


    public RecruitEntity findById(Long id) {
        RecruitEntity recruit = findOne(id);
        recruit.deserializeFields();
        return recruit;
    }

    /**
     * 投递简历
     */
    public void postResume(Long userId, Long recruitId, String title) {
        String json = JSONObject.toJSONString(new SendResume(userId, recruitId, title, LocalDateTime.now()));
        // 处理简历
        producer.send(Constant.HANDLE_RESUME, json);
        // 向接受方发送通知
        producer.send(Constant.RECEIVE_RESUME, json);
        // 向投递方发送通知
        producer.send(Constant.SEND_RESUME, json);
    }
}