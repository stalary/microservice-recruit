package com.stalary.pf.user;

import com.stalary.pf.user.data.entity.UserEs;
import com.stalary.pf.user.repo.UserEsRepo;
import com.stalary.pf.user.service.UserInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


/**
 * BaseTest
 *
 * @author lirongqian
 * @since 2018/10/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    @Resource
    private UserInfoService userInfoService;

    @Resource(name = "userEsRepo")
    private UserEsRepo esRepo;

    @Test
    public void testEs() {
//        userInfoService.saveEs(new UserEs(2L, "郑亚雯", "山东财经大学", "本科", "阿里巴巴,网易,腾讯", "前端开发工程师"));
        System.out.println(esRepo.findById(2L));
//        UserInfoService.saveEs(new UserEs(9L, "王树广", "山东财经大学", "本科", "百度","java开发工程师"));
//        UserInfoService.saveEs(new UserEs(3L, "邢奥林", "山东财经大学", "本科", "网易","java开发工程师,测试工程师"));
//        UserInfoService.saveEs(new UserEs(4L, "李荣谦", "山东财经大学", "本科", "网易","java开发工程师,大数据开发工程师,分布式架构师"));
//
    }

}