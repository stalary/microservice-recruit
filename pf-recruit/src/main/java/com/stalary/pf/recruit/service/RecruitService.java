package com.stalary.pf.recruit.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.lightmqclient.facade.Producer;
import com.stalary.pf.recruit.client.ResumeClient;
import com.stalary.pf.recruit.client.UserClient;
import com.stalary.pf.recruit.data.constant.Constant;
import com.stalary.pf.recruit.data.dto.RecruitDto;
import com.stalary.pf.recruit.data.dto.SendResume;
import com.stalary.pf.recruit.data.dto.User;
import com.stalary.pf.recruit.data.dto.UserInfo;
import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.*;
import com.stalary.pf.recruit.exception.MyException;
import com.stalary.pf.recruit.exception.ResultEnum;
import com.stalary.pf.recruit.repo.CompanyRepo;
import com.stalary.pf.recruit.repo.RecruitRepo;
import com.stalary.pf.recruit.util.RecruitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RecruitService
 *
 * @author lirongqian
 * @since 2018/04/17
 */
@Service
@Slf4j
public class RecruitService {

    @Resource
    private UserClient userClient;

    @Resource
    private ResumeClient resumeClient;

    @Resource
    private Producer producer;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;

    @Resource
    private RecruitRepo recruitRepo;

    @Resource
    private CompanyRepo companyRepo;

    /**
     * @param key  查询关键字
     * @param page 起始页码
     * @param size 每页数据量
     * @return 总数量和数据列表
     * @method allRecruit 分页查询招聘职位
     **/
    public Map<String, Object> getAllRecruit(String key, int page, int size) {
        Pair<Integer, Integer> pair = RecruitUtil.getStartAndEnd(page, size);
        List<RecruitEntity> allRecruit = getAllRecruit(key);
        List<RecruitEntity> recruitList = allRecruit.subList(pair.getLeft(), pair.getRight() <= allRecruit.size() ? pair.getRight() : allRecruit.size());
        List<RecruitAndCompany> ret = recruitList
                .stream()
                .map(r -> new RecruitAndCompany(r, getCompanyById(r.getCompanyId())))
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>(2, 1);
        result.put("total", allRecruit.size());
        result.put("recruitList", ret);
        return result;
    }

    /**
     * @return 职位名称列表
     * @method allRecruitName 获取所有职位名称
     **/
    public Set<String> getAllRecruitName() {
        return getAllRecruit("")
                .stream()
                .map(RecruitEntity::getTitle)
                .collect(Collectors.toSet());
    }

    public List<RecruitEntity> getAllRecruit(String key) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        Map<String, String> entries = redisHash.entries(Constant.RECRUIT_REDIS_PREFIX);
        if (!entries.isEmpty()) {
            // 获取全量的招聘信息
            if (StringUtils.isBlank(key)) {
                return entries
                        .entrySet()
                        .stream()
                        .map(e -> JSONObject.parseObject(e.getValue(), RecruitEntity.class))
                        .collect(Collectors.toList());
            } else {
                return entries
                        .entrySet()
                        .stream()
                        .map(e -> JSONObject.parseObject(e.getValue(), RecruitEntity.class))
                        .filter(c -> c.getTitle().contains(key))
                        .collect(Collectors.toList());
            }
        }
        List<RecruitEntity> recruitList;
        if (StringUtils.isBlank(key)) {
            recruitList = recruitRepo.findAll();
            recruitList.forEach(RecruitEntity::deserializeFields);
            if (!recruitList.isEmpty()) {
                Map<String, String> redisMap = recruitList
                        .stream()
                        .collect(Collectors.toMap(r -> String.valueOf(r.getId()), JSONObject::toJSONString));
                redisHash.putAll(Constant.RECRUIT_REDIS_PREFIX, redisMap);
                // 缓存七天
                redis.expire(Constant.RECRUIT_REDIS_PREFIX, 7, TimeUnit.DAYS);
            }
            return recruitList;
        }
        // 由于做全量缓存，所以模糊查询时不进行缓存
        return recruitRepo.findByTitleIsLike("%" + key + "%");
    }

    /**
     * @param id 职位id
     * @return 职位-hr-公司信息
     * @method getRecruitInfo 获取职位详细信息
     **/
    public RecruitAndHrAndCompany getRecruitInfo(Long id) {
        RecruitEntity recruit = getRecruitById(id);
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
        RecruitEntity save = recruitRepo.save(recruit);
        save.deserializeFields();
        // 写入缓存
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        redisHash.put(Constant.RECRUIT_REDIS_PREFIX, String.valueOf(save.getId()), JSONObject.toJSONString(save));
        // 缓存七天
        redis.expire(Constant.RECRUIT_REDIS_PREFIX, 7, TimeUnit.DAYS);
        return save;
    }

    public void deleteById(Long id) {
        // 删除对应缓存
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        redisHash.delete(Constant.RECRUIT_REDIS_PREFIX, String.valueOf(id));
        recruitRepo.deleteById(id);
    }

    /**
     * @param userId 用户id
     * @return 职位列表
     * @method findByUserId 根据用户id获取已发布的职位
     **/
    public List<RecruitEntity> getRecruitByUserId(Long userId) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        Map<String, String> entries = redisHash.entries(Constant.RECRUIT_REDIS_PREFIX);
        if (!entries.isEmpty()) {
            return entries
                    .entrySet()
                    .stream()
                    .map(e -> JSONObject.parseObject(e.getValue(), RecruitEntity.class))
                    .filter(e -> e.getHrId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            List<RecruitEntity> recruitList = recruitRepo.findByHrId(userId);
            recruitList.forEach(RecruitEntity::deserializeFields);
            return recruitList;
        }
    }

    /**
     * 通过id查找招聘信息
     **/
    public RecruitEntity getRecruitById(Long id) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        RecruitEntity recruitRet;
        if (redisHash.hasKey(Constant.RECRUIT_REDIS_PREFIX, String.valueOf(id))) {
            recruitRet = JSONObject.parseObject(redisHash.get(Constant.RECRUIT_REDIS_PREFIX, String.valueOf(id)), RecruitEntity.class);
        } else {
            Optional<RecruitEntity> recruit = recruitRepo.findById(id);
            if (recruit.isPresent()) {
                recruitRet = recruit.get();
                recruitRet.deserializeFields();
            } else {
                throw new MyException(ResultEnum.RECRUIT_NOT_EXIST);
            }
        }
        return recruitRet;
    }

    /**
     * 获取所有公司的名称
     **/
    public List<String> getAllCompanyName() {
        return getAllCompany("")
                .stream()
                .map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }

    /**
     * 添加公司
     **/
    public CompanyEntity addCompany(CompanyEntity entity) {
        // 首先写入缓存，然后存到数据库
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        redisHash.put(Constant.COMPANY_REDIS_PREFIX, String.valueOf(entity.getId()), JSONObject.toJSONString(entity));
        // 缓存七天
        redis.expire(Constant.COMPANY_REDIS_PREFIX, 7, TimeUnit.DAYS);
        return companyRepo.save(entity);
    }

    public Map<String, Object> getAllCompanyByPage(int page, int size) {
        List<CompanyEntity> allCompany = getAllCompany("");
        Pair<Integer, Integer> pair = RecruitUtil.getStartAndEnd(page, size);
        Map<String, Object> ret = new HashMap<>();
        ret.put("total", allCompany.size());
        ret.put("companyList", allCompany.subList(pair.getLeft(), pair.getRight() <= allCompany.size() ? pair.getRight() : allCompany.size()));
        return ret;
    }

    /**
     * 获取所有公司
     **/
    public List<CompanyEntity> getAllCompany(String key) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        Map<String, String> entries = redisHash.entries(Constant.COMPANY_REDIS_PREFIX);
        if (!entries.isEmpty()) {
            // 获取全量的公司
            if (StringUtils.isBlank(key)) {
                return entries
                        .entrySet()
                        .stream()
                        .map(e -> JSONObject.parseObject(e.getValue(), CompanyEntity.class))
                        .collect(Collectors.toList());
            } else {
                return entries
                        .entrySet()
                        .stream()
                        .map(e -> JSONObject.parseObject(e.getValue(), CompanyEntity.class))
                        .filter(c -> c.getName().contains(key))
                        .collect(Collectors.toList());
            }
        }
        List<CompanyEntity> companyList;
        if (StringUtils.isBlank(key)) {
            companyList = companyRepo.findAll();
            // 未命中缓存时查询到数据时插入缓存中
            if (!companyList.isEmpty()) {
                Map<String, String> redisMap = companyList
                        .stream()
                        .collect(Collectors.toMap(c -> String.valueOf(c.getId()), JSONObject::toJSONString));
                redisHash.putAll(Constant.COMPANY_REDIS_PREFIX, redisMap);
                // 缓存七天
                redis.expire(Constant.COMPANY_REDIS_PREFIX, 7, TimeUnit.DAYS);
            }
            return companyList;
        }
        // 由于做全量缓存，所以模糊查询时不进行缓存
        return companyRepo.findByNameIsLike("%" + key + "%");
    }

    public CompanyAndRecruit getCompanyInfo(Long id) {
        CompanyEntity company = getCompanyById(id);
        List<RecruitEntity> recruitList = recruitRepo.findByCompanyId(id);
        return new CompanyAndRecruit(company, recruitList);
    }

    private CompanyEntity getCompanyById(Long id) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        CompanyEntity company;
        if (redisHash.hasKey(Constant.COMPANY_REDIS_PREFIX, String.valueOf(id))) {
            company = JSONObject.parseObject(redisHash.get(Constant.COMPANY_REDIS_PREFIX, String.valueOf(id)), CompanyEntity.class);
        } else {
            company = companyRepo.findById(id).orElse(null);
        }
        return company;
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

    /**
     * 获取推荐候选人
     **/
    public List<RecommendCandidate> getRecommendCandidate(Long userId) {
        List<RecommendCandidate> ret = new ArrayList<>();
        // 获取当前用户下的的所有职位
        List<RecruitEntity> recruitList = getRecruitByUserId(userId);
        recruitList.forEach(recruit -> {
            CompanyEntity company = getCompanyById(recruit.getCompanyId());
            if (company != null) {
                // 首先通过职位和公司名称去匹配用户
                String job = recruit.getTitle();
                String companyName = company.getName();
                List<UserInfo> userInfoList = userClient.getRecommendCandidate(companyName, job).getData();
                List<Candidate> resultList = new ArrayList<>();
                userInfoList.forEach(userInfo -> {
                    // 公司和职位全匹配的在前面
                    Candidate candidate = Candidate.init(userInfo);
                    RecruitDto recruitDto = new RecruitDto();
                    recruitDto.setId(recruit.getId());
                    recruitDto.setSkillList(recruit.getSkillList());
                    Integer rate = resumeClient.getRate(userInfo.getUserId(), recruitDto).getData();
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

    /**
     * 获取推荐职位
     **/
    public List<RecommendRecruit> getRecommendJob(Long userId) {
        UserInfo userInfo = userClient.getUserInfo(userId).getData();
        List<String> jobList = str2List(userInfo.getIntentionJob());
        List<String> companyList = str2List(userInfo.getIntentionCompany());
        List<RecruitEntity> recruitList = recruitRepo.findByTitleIn(jobList);
        // 当推荐的职位所在公司同时为意向公司时，排名考前
        List<RecommendRecruit> firstList = new ArrayList<>();
        List<RecommendRecruit> secondList = new ArrayList<>();
        recruitList.forEach(recruit -> {
            CompanyEntity company = getCompanyById(recruit.getCompanyId());
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