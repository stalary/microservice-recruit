package com.stalary.pf.recruit.controller;

import com.stalary.pf.recruit.data.dto.SendResume;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import com.stalary.pf.recruit.service.RecruitService;
import com.stalary.pf.recruit.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * RecruitController
 *

 */

/**
 * @model RecruitController
 * @description 招聘相关接口
 * @author lirongqian
 * @since 2018/04/17
 **/
@RestController
@RequestMapping("/recruit")
@Slf4j
public class RecruitController {

    @Resource
    private RecruitService recruitService;

    /**
     * @method add 添加招聘信息
     * @param recruit 招聘信息对象
     * @return RecruitEntity 招聘信息对象
     **/
    @PostMapping
    public ResponseMessage add(
            @RequestBody RecruitEntity recruit) {
        RecruitEntity saveRecruit = recruitService.saveRecruit(recruit);
        return ResponseMessage.successMessage(saveRecruit);
    }

    /**
     * @method delete 删除招聘信息
     * @param id 招聘信息id
     **/
    @DeleteMapping
    public ResponseMessage delete(
            @RequestParam Long id) {
        recruitService.deleteById(id);
        return ResponseMessage.successMessage();
    }

    /**
     * @method allRecruit 查看所有招聘信息
     * @param key 关键字
     * @param page 当前页数
     * @param size 每页数据量
     * @return RecruitAndCompany 招聘信息
     **/
    @GetMapping
    public ResponseMessage allRecruit(
            @RequestParam(required = false, defaultValue = "") String key,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size) {
        return ResponseMessage.successMessage(recruitService.allRecruit(key, page, size));
    }

    @GetMapping("/list")
    public ResponseMessage recruitList(
            @RequestParam Long userId) {
        return ResponseMessage.successMessage(recruitService.findByUserId(userId));
    }

    /**
     * @method getInfo 查看招聘信息
     * @param id 岗位id
     * @return RecruitAndHrAndCompany 招聘信息
     **/
    @GetMapping("/{id}")
    public ResponseMessage getInfo(
            @PathVariable("id") Long id) {
        return ResponseMessage.successMessage(recruitService.getRecruitInfo(id));
    }

    /**
     * @method getHrInfo 获取当前hr的招聘信息
     * @return RecruitEntity 招聘信息
     **/
    @GetMapping("/hr")
    public ResponseMessage getHrInfo(
            HttpServletRequest request) {
        return ResponseMessage.successMessage(recruitService.findByUserId(UserUtil.getUserId(request)));
    }

    /**
     * 投递简历的步骤
     * 1 投递简历，在mapdb中构成投递表
     * 2 向hr发送简历接收通知(站内信，邮件)
     * 3 向投递者发送简历投递成功的通知
     * 4 向hr和投递者push更新后的未读通知数量
     *
     */
    /**
     * @method postResume 投递简历
     * @param sendResume 投递简历对象
     **/
    @PostMapping("/resume")
    public ResponseMessage postResume(
            HttpServletRequest request,
            @RequestBody SendResume sendResume) {
        log.info("sendResume" + sendResume);
        recruitService.postResume(UserUtil.getUserId(request), sendResume.getRecruitId(), sendResume.getTitle());
        return ResponseMessage.successMessage("投递成功");
    }

}