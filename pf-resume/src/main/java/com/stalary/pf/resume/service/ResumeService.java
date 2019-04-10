package com.stalary.pf.resume.service;

import com.alibaba.fastjson.JSONObject;
import com.stalary.pf.resume.client.RecruitClient;
import com.stalary.pf.resume.client.UserClient;
import com.stalary.pf.resume.data.constant.Constant;
import com.stalary.pf.resume.data.constant.RedisKeys;
import com.stalary.pf.resume.data.dto.*;
import com.stalary.pf.resume.data.entity.Resume;
import com.stalary.pf.resume.data.entity.Skill;
import com.stalary.pf.resume.exception.MyException;
import com.stalary.pf.resume.exception.ResultEnum;
import com.stalary.pf.resume.repo.ResumeRepo;
import com.stalary.pf.resume.repo.SkillRepo;
import com.stalary.pf.resume.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ResumeService
 *
 * @author lirongqian
 * @since 2018/04/14
 */
@Service
@Slf4j
public class ResumeService extends BaseService<Resume, ResumeRepo> {

    public ResumeService(ResumeRepo repo) {
        super(repo);
    }

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongo;

    @Resource(name = "skillRepo")
    private SkillRepo skillRepo;

    @Resource
    private RecruitClient recruitClient;

    @Resource
    private UserClient userClient;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redis;


    /**
     * 处理简历
     */
    public void handleResume(SendResume sendResume) {
        log.info("start handle resume");
        HashOperations<String, String, String> redisHash = redis.opsForHash();
        long start = System.currentTimeMillis();
        Long userId = sendResume.getUserId();
        Long recruitId = sendResume.getRecruitId();
        // 构建发送列表
        String sendKey = Constant.getKey(RedisKeys.RESUME_SEND, String.valueOf(userId));
        redisHash.put(sendKey, String.valueOf(recruitId), JSONObject.toJSONString(sendResume));
        // 构建获取列表
        String receiveKey = Constant.getKey(RedisKeys.RESUME_RECEIVE, String.valueOf(recruitId));
        UserInfo userInfo = userClient.getUserInfo(userId).getData();
        // 计算匹配度
        Recruit recruit = recruitClient.getRecruit(recruitId).getData();
        int rate = calculate(recruit, userId);
        ReceiveResume receiveResume = new ReceiveResume(sendResume.getTitle(), userInfo.getNickname(), userInfo.getUserId(), rate, LocalDateTime.now());
        redisHash.put(receiveKey, String.valueOf(userId), JSONObject.toJSONString(receiveResume));
        log.info("end handle resume spend time is " + (System.currentTimeMillis() - start));
    }

    /**
     * 保存简历
     */
    public Resume saveResume(Resume resume) {
        final long resumeId = IdUtil.getNextId(Resume.class.getSimpleName(), mongo);
        skillRepo.saveAll(
                resume.getSkills()
                        .stream()
                        .peek(skill -> {
                            // 存入简历id
                            if (skill.getResumeId() == 0) {
                                skill.setResumeId(resumeId);
                            }
                        })
                        .collect(Collectors.toList())
        );
        return repo.save(resume);
    }

    public Resume findByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    /**
     * 批量获取简历分数
     **/
    public List<ResumeRate> batchCalculate(GetResumeRate getResumeRate) {
        List<ResumeRate> ret = new ArrayList<>();
        List<Long> userIdList = getResumeRate.getGetList().stream().map(GetResumeRate.GetRate::getUserId).collect(Collectors.toList());
        // 批量获取简历
        Map<Long, Resume> resumeMap = getResumeMap(userIdList);
        getResumeRate
                .getGetList()
                .forEach(g -> ret.add(new ResumeRate(g.getUserId(),
                        g.getRecruit().getId(),
                        calculate(g.getRecruit(), resumeMap.get(g.getUserId())))));
        return ret;
    }

    private Map<Long, Resume> getResumeMap(List<Long> userIdList) {
        return repo.findByUserIdIn(userIdList).stream().collect(Collectors.toMap(Resume::getUserId, r -> r));
    }

    public int calculate(Recruit recruit, Long userId) {
        Resume resume = repo.findByUserId(userId);
        return calculate(recruit, resume);
    }

    /**
     * 简历打分
     */
    private int calculate(Recruit recruit, Resume resume) {
        if (resume == null) {
            return 0;
        }
        List<SkillRule> skillRuleList = recruit.getSkillList();
        List<Skill> skillList = resume.getSkills();
        // 求出规则表中总和
        int ruleSum = skillRuleList
                .stream()
                .mapToInt(SkillRule::getWeight)
                .sum();
        // 求出规则表中的技能点
        List<String> nameRuleList = skillRuleList
                .stream()
                .map(SkillRule::getName)
                .collect(Collectors.toList());
        // 求出简历表中的技能点
        List<String> nameList = skillList
                .stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
        // 求出技能点交集
        List<String> intersection = nameRuleList
                .stream()
                .filter(nameList::contains)
                .collect(Collectors.toList());
        // 生成规则表的映射
        Map<String, Integer> nameRuleMap = skillRuleList
                .stream()
                .collect(Collectors.toMap(SkillRule::getName, SkillRule::getWeight));
        // 命中的和
        int getRuleSum = intersection
                .stream()
                .mapToInt(nameRuleMap::get)
                .sum();
        // 规则占比
        double rulePercent = (double) getRuleSum / ruleSum;
        // 技能点总和
        int sum = intersection.size() * 4;
        // 生成技能点的映射
        Map<String, Integer> nameMap = skillList
                .stream()
                .collect(Collectors.toMap(Skill::getName, Skill::getLevel));
        // 命中技能点的和
        int getSum = intersection
                .stream()
                .mapToInt(nameMap::get)
                .sum();
        // 技能点占比
        double percent = (double) getSum / sum;
        return (int) Math.round(percent * rulePercent * 100);
    }

}