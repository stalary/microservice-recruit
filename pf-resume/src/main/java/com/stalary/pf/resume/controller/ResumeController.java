package com.stalary.pf.resume.controller;

import com.stalary.pf.resume.data.dto.GetResumeRate;
import com.stalary.pf.resume.data.dto.Recruit;
import com.stalary.pf.resume.data.dto.SendResume;
import com.stalary.pf.resume.data.entity.Resume;
import com.stalary.pf.resume.data.vo.ResponseMessage;
import com.stalary.pf.resume.service.ResumeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * ResumeController
 *
 * @author lirongqian
 * @description 简历操作接口
 * @since 2018/04/16
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    /**
     * @param resume 简历对象
     * @return Resume 简历对象
     * @method save  保存修改简历
     **/
    @PostMapping
    public ResponseMessage save(
            @RequestBody Resume resume) {
        return ResponseMessage.successMessage(resumeService.saveResume(resume));
    }

    /**
     * @param userId 用户id
     * @return Resume 简历对象
     * @method getResumeByUserId 通过userId查找对应简历
     **/
    @GetMapping
    public ResponseMessage getResumeByUserId(
            @RequestParam Long userId) {
        return ResponseMessage.successMessage(resumeService.findByUserId(userId));
    }

    /**
     * @param sendResume 处理简历
     * @method handleResume
     **/
    @PostMapping("/handle")
    public ResponseMessage handleResume(
            @RequestBody SendResume sendResume) {
        resumeService.handleResume(sendResume);
        return ResponseMessage.successMessage("简历处理成功");
    }

    /**
     * @param userId    用户id
     * @param recruit 职位信息
     * @return rate 匹配度
     * @method getRate 获取职位匹配程度
     **/
    @PostMapping("/rate")
    public ResponseMessage getRate(
            @RequestParam Long userId,
            @RequestBody Recruit recruit) {
        return ResponseMessage.successMessage(resumeService.calculate(recruit, userId));
    }

    @PostMapping("/rate/batch")
    public ResponseMessage getRateBatch(
            @RequestBody GetResumeRate getResumeRate) {
        return ResponseMessage.successMessage(resumeService.batchCalculate(getResumeRate));
    }
}