package com.stalary.pf.recruit.controller;

import com.stalary.pf.recruit.annotation.LoginRequired;
import com.stalary.pf.recruit.data.entity.RecruitEntity;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import com.stalary.pf.recruit.holder.UserHolder;
import com.stalary.pf.recruit.service.RecruitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    @LoginRequired
    public ResponseMessage getHrInfo() {
        return ResponseMessage.successMessage(recruitService.findByUserId(UserHolder.get()));
    }
}