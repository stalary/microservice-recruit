package com.stalary.pf.resume.controller;

import com.stalary.pf.resume.data.dto.SendResume;
import com.stalary.pf.resume.data.entity.Resume;
import com.stalary.pf.resume.data.vo.ResponseMessage;
import com.stalary.pf.resume.service.ResumeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * ResumeController
 *
 * @description 简历操作接口
 * @author lirongqian
 * @since 2018/04/16
 */
@RestController
@RequestMapping("/resume")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    /**
     * @method save  保存修改简历
     * @param resume 简历对象
     * @return Resume 简历对象
     **/
    @PostMapping
    public ResponseMessage save(
            @RequestBody Resume resume) {
        return ResponseMessage.successMessage(resumeService.saveResume(resume));
    }

    /**
     * @method getResumeByUserId 通过userId查找对应简历
     * @param userId 用户id
     * @return Resume 简历对象
     **/
    @GetMapping
    public ResponseMessage getResumeByUserId(
            @RequestParam Long userId) {
        return ResponseMessage.successMessage(resumeService.findByUserId(userId));
    }

    /**
     * @method handleResume
     * @param sendResume 处理简历
     **/
    @PostMapping("/handle")
    public ResponseMessage handleResume(
            @RequestBody SendResume sendResume) {
        resumeService.handleResume(sendResume);
        return ResponseMessage.successMessage("简历处理成功");
    }

    /**
     * @method getRate 获取职位匹配程度
     * @param userId 用户id
     * @param recruitId 职位id
     * @return rate 匹配度
     **/
    @GetMapping("/rate")
    public ResponseMessage getRate(
            @RequestParam Long userId,
            @RequestParam Long recruitId) {
        return ResponseMessage.successMessage(resumeService.calculate(recruitId, userId));
    }
}