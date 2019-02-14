package com.stalary.pf.user;

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
    private UserInfoService UserInfoService;

    @Test
    public void test() {
//        userService.save(new User(1L, "李荣谦", "man", "阿里巴巴,网易,腾讯", "山东财经大学", "本科", "java开发工程师,大数据开发工程师,分布式架构师"));
//        userService.save(new User(2L,  "郑亚雯", "woman", "网易", "山东财经大学", "本科", "前端工程师"));
//        userService.save(new User(3L,  "王树广", "man", "百度", "山东财经大学", "本科", "java开发工程师"));
//        userService.save(new User(4L, "邢奥林", "woman", "网易", "山东财经大学", "本科", "java开发工程师,测试工程师"));
//        long start = System.currentTimeMillis();
//        System.out.println(userService.findByCompany("阿里", "java"));
//        System.out.println("es time." + (System.currentTimeMillis() - start));
//        long start1 = System.currentTimeMillis();
//        System.out.println(userService.findByCompany4Mysql("阿里", "java"));
//        System.out.println("mysql time." + (System.currentTimeMillis() - start1));
    }
}