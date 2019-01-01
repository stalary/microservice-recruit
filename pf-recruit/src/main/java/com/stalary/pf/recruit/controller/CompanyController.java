package com.stalary.pf.recruit.controller;

import com.stalary.pf.recruit.data.entity.CompanyEntity;
import com.stalary.pf.recruit.data.vo.ResponseMessage;
import com.stalary.pf.recruit.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * CompanyController
 *
 * @description 公司相关接口
 * @author lirongqian
 * @since 2018/04/14
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    /**
     * @method allCompany 查找所有公司，分页
     * @param page 当前页数
     * @param size 每页数据量
     * @return Company 公司列表
     */
    @GetMapping
    public ResponseMessage allCompany(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "4") int size) {
        return ResponseMessage.successMessage(companyService.allCompany(page, size));
    }

    /**
     * @method allCompanyNoPage 不分页查找所有公司，用于获取公司列表,传入key则按关键字查找
     * @param key 关键字
     * @return 0 公司列表
     **/
    @GetMapping("/noPage")
    public ResponseMessage allCompanyNoPage(
            @RequestParam(required = false, defaultValue = "") String key) {
        return ResponseMessage.successMessage(companyService.findCompany(key));
    }

    /**
     * @method addCompany 添加公司
     * @param company 公司对象
     * @return Company 公司对象
     */
    @PostMapping
    public ResponseMessage addCompany(
            @RequestBody CompanyEntity company) {
        return ResponseMessage.successMessage(companyService.save(company));
    }


    /**
     * @method getInfo 查看公司详情
     * @param id 公司id
     * @return CompanyAndRecruit 公司详情
     **/
    @GetMapping("{id}")
    public ResponseMessage getInfo(
            @PathVariable("id") Long id) {
        return ResponseMessage.successMessage(companyService.getInfo(id));
    }
}