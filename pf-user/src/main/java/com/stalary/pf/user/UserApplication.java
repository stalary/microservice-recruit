package com.stalary.pf.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UserApplication {

    public static void main(String[] args) {
        // 解决es中的netty冲突
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(UserApplication.class, args);
    }

}

