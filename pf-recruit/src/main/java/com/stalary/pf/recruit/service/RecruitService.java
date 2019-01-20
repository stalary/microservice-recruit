package com.stalary.pf.recruit.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.lightmqclient.facade.Producer;
import com.stalary.pf.recruit.client.ResumeClient;
import com.stalary.pf.recruit.client.UserClient;
import com.stalary.pf.recruit.data.constant.Constant;
import com.stalary.pf.recruit.data.dto.SendResume;
import com.stalary.pf.recruit.data.dto.User;
import com.stalary.pf.recruit.data.dto.UserInfo;
import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.*;
import com.stalary.pf.recruit.repo.CompanyRepo;
import com.stalary.pf.recruit.repo.RecruitRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private ResumeClient resumeClient;

    @Resource
    private CompanyRepo companyRepo;

    @Resource
    private Producer producer;

    /**
     * @param key  查询关键字
     * @param page 起始页码
     * @param size 每页数据量
     * @return 总数量和数据列表
     * @method allRecruit 分页查询招聘职位
     **/
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
            list.add(new RecruitAndCompany(recruit, companyRepo.findById(recruit.getCompanyId()).orElse(null)));
        });
        Map<String, Object> result = new HashMap<>();
        result.put("total", recruitPage.getTotalPages());
        result.put("recruitList", list);
        return result;
    }

    /**
     * @method allRecruitName 获取所有职位名称
     * @return 职位名称列表
     **/
    public Set<String> allRecruitName() {
        List<RecruitEntity> all = repo.findAll();
        return all.stream().map(RecruitEntity::getTitle).collect(Collectors.toSet());
    }

    /**
     * @param id 职位id
     * @return 职位-hr-公司信息
     * @method getRecruitInfo 获取职位详细信息
     **/
    public RecruitAndHrAndCompany getRecruitInfo(Long id) {
        RecruitEntity recruit = findById(id);
        User user = userClient.getUser(recruit.getHrId()).getData();
        HR hr = new HR().toBuilder()
                .companyId(recruit.getCompanyId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .username(user.getUsername()).build();
        CompanyEntity company = companyRepo.findById(recruit.getCompanyId()).orElse(null);
        return new RecruitAndHrAndCompany(recruit, hr, company);
    }

    /**
     * @param recruit 职位信息
     * @return 职位对象
     * @method saveRecruit 保存职位
     **/
    public RecruitEntity saveRecruit(RecruitEntity recruit) {
        recruit.serializeFields();
        RecruitEntity save = repo.save(recruit);
        save.deserializeFields();
        return save;
    }

    /**
     * @param userId 用户id
     * @return 职位列表
     * @method findByUserId 根据用户id获取已发布的职位
     **/
    public List<RecruitEntity> findByUserId(Long userId) {
        List<RecruitEntity> recruitList = repo.findByHrId(userId);
        recruitList.forEach(RecruitEntity::deserializeFields);
        return recruitList;
    }


    private RecruitEntity findById(Long id) {
        RecruitEntity recruit = findOne(id);
        recruit.deserializeFields();
        return recruit;
    }

    /**
     * @param userId    用户id
     * @param recruitId 招聘id
     * @param title     招聘标题
     * @method postResume 投递简历
     **/
    public void postResume(Long userId, Long recruitId, String title) {
        String json = JSONObject.toJSONString(new SendResume(userId, recruitId, title, LocalDateTime.now()));
        // 处理简历
        producer.send(Constant.HANDLE_RESUME, json);
        // 向接受方发送通知
        producer.send(Constant.RECEIVE_RESUME, json);
        // 向投递方发送通知
        producer.send(Constant.SEND_RESUME, json);
    }

    public List<RecommendCandidate> getRecommendCandidate(Long userId) {
        List<RecommendCandidate> ret = new ArrayList<>();
        // 获取当前用户下的的所有职位
        List<RecruitEntity> recruitList = findByUserId(userId);
        recruitList.forEach(recruit -> {
            CompanyEntity company = companyRepo.findById(recruit.getCompanyId()).orElse(null);
            if (company != null) {
                // 首先通过职位和公司名称去匹配用户
                String job = recruit.getTitle();
                String companyName = company.getName();
                List<UserInfo> userInfoList = userClient.getRecommendCandidate(companyName, job).getData();
                List<Candidate> resultList = new ArrayList<>();
                userInfoList.forEach(userInfo -> {
                    // 公司和职位全匹配的在前面
                    Candidate candidate = Candidate.init(userInfo);
                    Integer rate = resumeClient.getRate(userInfo.getUserId(), recruit.getId()).getData();
                    candidate.setRate(rate == null ? 0 : rate);
                    resultList.add(candidate);
                });
                // 按照简历匹配程度排序
                if (!resultList.isEmpty()) {
                    resultList.sort(Comparator.comparing(Candidate::getRate).reversed());
                }
                ret.add(new RecommendCandidate(job, resultList));
            }
        });
        return ret;
    }

    public List<RecommendRecruit> getRecommendJob(Long userId) {
        UserInfo userInfo = userClient.getUserInfo(userId).getData();
        List<String> jobList = str2List(userInfo.getIntentionJob());
        List<String> companyList = str2List(userInfo.getIntentionCompany());
        List<RecruitEntity> recruitList = repo.findByTitleIn(jobList);
        // 当推荐的职位所在公司同时为意向公司时，排名考前
        List<RecommendRecruit> firstList = new ArrayList<>();
        List<RecommendRecruit> secondList = new ArrayList<>();
        recruitList.forEach(recruit -> {
            CompanyEntity company = companyRepo.findById(recruit.getCompanyId()).orElse(null);
            if (company != null) {
                if (companyList.contains(company.getName())) {
                    RecommendRecruit recommendRecruit = new RecommendRecruit(recruit.getId(), recruit.getTitle(), company.getName());
                    firstList.add(recommendRecruit);
                } else {
                    RecommendRecruit recommendRecruit = new RecommendRecruit(recruit.getId(), recruit.getTitle(), company.getName());
                    secondList.add(recommendRecruit);
                }
            }
        });
        firstList.addAll(secondList);
        return firstList;
    }

    private List<String> str2List(String str) {
        String[] split = str.split(",");
        return Arrays.stream(split).collect(Collectors.toList());
    }

}