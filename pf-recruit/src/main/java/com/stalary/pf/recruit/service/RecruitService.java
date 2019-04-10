package com.stalary.pf.recruit.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.lightmqclient.facade.Producer;
import com.stalary.pf.recruit.client.ResumeClient;
import com.stalary.pf.recruit.client.UserClient;
import com.stalary.pf.recruit.data.constant.Constant;
import com.stalary.pf.recruit.data.dto.*;
import com.stalary.pf.recruit.data.entity.BaseEntity;
import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.*;
import com.stalary.pf.recruit.exception.ExceptionThreadFactory;
import com.stalary.pf.recruit.exception.MyException;
import com.stalary.pf.recruit.exception.ResultEnum;
import com.stalary.pf.recruit.repo.CompanyRepo;
import com.stalary.pf.recruit.repo.RecruitRepo;
import com.stalary.pf.recruit.util.RecruitUtil;
import lombok.Getter;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
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

    private ExecutorService exec = new ThreadPoolExecutor(
            3,
            10,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50),
            new ExceptionThreadFactory(RecruitService.this.getClass().getSimpleName(), false),
            // 达到最大容量后，直接抛出异常(服务降级)
            new ThreadPoolExecutor.AbortPolicy()
    );

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

    private List<RecruitEntity> getAllRecruit(String key) {
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
            cacheRecruit(recruitList);
            return recruitList;
        }
        // 由于做全量缓存，所以模糊查询时不进行缓存
        return recruitRepo.findByTitleIsLike("%" + key + "%");
    }

    private void cacheRecruit(List<RecruitEntity> recruitList) {
        if (!recruitList.isEmpty()) {
            HashOperations<String, String, String> redisHash = redis.opsForHash();
            Map<String, String> redisMap = recruitList
                    .stream()
                    .collect(Collectors.toMap(r -> String.valueOf(r.getId()), JSONObject::toJSONString));
            redisHash.putAll(Constant.RECRUIT_REDIS_PREFIX, redisMap);
            // 缓存七天
            redis.expire(Constant.RECRUIT_REDIS_PREFIX, 7, TimeUnit.DAYS);
        }
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
        CompanyEntity company = getCompanyById(recruit.getCompanyId());
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
        // 异步刷新全量缓存
        exec.execute(() -> {
            List<RecruitEntity> recruitList = recruitRepo.findAll();
            recruitList.forEach(RecruitEntity::deserializeFields);
            cacheRecruit(recruitList);
        });
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
        CompanyEntity save = companyRepo.save(entity);
        // 异步更新全量缓存
        exec.execute(() -> {
            List<CompanyEntity> companyList = companyRepo.findAll();
            cacheCompany(companyList);
        });
        return save;
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
            cacheCompany(companyList);
            return companyList;
        }
        // 由于做全量缓存，所以模糊查询时不进行缓存
        return companyRepo.findByNameIsLike("%" + key + "%");
    }

    private void cacheCompany(List<CompanyEntity> companyList) {
        if (!companyList.isEmpty()) {
            HashOperations<String, String, String> redisHash = redis.opsForHash();
            Map<String, String> redisMap = companyList
                    .stream()
                    .collect(Collectors.toMap(c -> String.valueOf(c.getId()), JSONObject::toJSONString));
            redisHash.putAll(Constant.COMPANY_REDIS_PREFIX, redisMap);
            // 缓存七天
            redis.expire(Constant.COMPANY_REDIS_PREFIX, 7, TimeUnit.DAYS);
        }
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
        // 获取职位对应的公司
        List<CompanyAndJob> companyAndJobList = recruitList
                .stream()
                .map(r -> {
                    CompanyEntity company = getCompanyById(r.getCompanyId());
                    if (company != null) {
                        return new CompanyAndJob(r.getId(), company.getName(), r.getTitle());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 生成招聘信息map
        Map<Long, RecruitEntity> recruitMap = recruitList
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, r -> r));
        // 获取所有职位和公司对应的候选人列表
        List<RecommendUser> recommendCandidate = userClient.getRecommendCandidate(JSONObject.toJSONString(companyAndJobList)).getData();
        List<GetResumeRate.GetRate> getRateList = new ArrayList<>();
        recommendCandidate.forEach(c -> {
            long recruitId = c.getRecruitId();
            c.getUserList().forEach(u -> {
                RecruitEntity recruit = recruitMap.get(recruitId);
                if (recruit != null) {
                    RecruitDto recruitDto = new RecruitDto();
                    recruitDto.setId(recruit.getId());
                    recruitDto.setSkillList(recruit.getSkillList());
                    getRateList.add(new GetResumeRate.GetRate(u.getUserId(), recruitDto));
                }
            });
        });
        // 获取所有候选人对应的简历匹配分数
        List<ResumeRate> rateList = resumeClient.getRate(new GetResumeRate(getRateList)).getData();
        recommendCandidate.forEach(r -> {
            Long recruitId = r.getRecruitId();
            // 过滤出当前职位对应的候选人分数
            List<ResumeRate> filterRateList = rateList
                    .stream()
                    .filter(resume -> resume.getRecruitId().equals(recruitId))
                    .collect(Collectors.toList());
            RecruitEntity recruit = recruitMap.get(recruitId);
            if (recruit != null) {
                List<Candidate> candidateList = new ArrayList<>();
                String title = recruit.getTitle();
                r.getUserList().forEach((userInfo -> {
                    // 选中当前候选人的分数
                    Optional<ResumeRate> resumeRate = filterRateList
                            .stream()
                            .filter(rate -> rate.getUserId().equals(userInfo.getUserId()))
                            .findFirst();
                    if (resumeRate.isPresent()) {
                        Candidate candidate = Candidate.init(userInfo);
                        Integer rate = resumeRate.get().getRate();
                        // 过滤掉rate为0的
                        if (rate > 0) {
                            candidate.setRate(resumeRate.get().getRate());
                            candidateList.add(candidate);
                        }
                    }
                }));
                // 按照简历匹配程度排序
                if (!candidateList.isEmpty()) {
                    candidateList.sort(Comparator.comparing(Candidate::getRate).reversed());
                }
                //  只返回前三个
                if (candidateList.size() > 3) {
                    ret.add(new RecommendCandidate(title, candidateList.subList(0, 3)));
                } else {
                    ret.add(new RecommendCandidate(title, candidateList));
                }
            }
        });
        return ret;
    }

    /**
     * 获取推荐职位
     **/
    public List<RecommendRecruit> getRecommendJob(Long userId) {
        UserInfo userInfo = userClient.getUserInfo(userId).getData();
        // 获取意向职位
        List<String> jobList = str2List(userInfo.getIntentionJob());
        // 获取意向公司
        List<String> companyList = str2List(userInfo.getIntentionCompany());
        // 获取匹配的职位
        List<RecruitEntity> recruitList = findRecruitByTitleIn(jobList);
        // 当推荐的职位所在公司同时为意向公司时，排名靠前
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
        // 只获取前三个职位
        if (firstList.size() > 3) {
            return firstList.subList(0, 3);
        }
        return firstList;
    }

    private List<RecruitEntity> findRecruitByTitleIn(List<String> jobList) {
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        Map<String, String> entries = redisHash.entries(Constant.RECRUIT_REDIS_PREFIX);
        if (!entries.isEmpty()) {
            return entries
                    .entrySet()
                    .stream()
                    .map(e -> JSONObject.parseObject(e.getValue(), RecruitEntity.class))
                    .filter(r -> jobList.contains(r.getTitle()))
                    .collect(Collectors.toList());
        }
        return recruitRepo.findByTitleIn(jobList);
    }

    private List<String> str2List(String str) {
        String[] split = str.split(",");
        return Arrays.stream(split).collect(Collectors.toList());
    }

}