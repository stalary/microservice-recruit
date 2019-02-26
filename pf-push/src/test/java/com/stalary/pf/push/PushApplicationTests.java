package com.stalary.pf.push;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PushApplicationTests {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

//    @Test
//    public void testRedis() throws InterruptedException {
//        redisTemplate.convertAndSend("course_lesson_report", "测试");
//        Thread.sleep(10000);
//    }

}

