package com.nguyenthanhbang.Social_media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication
@EnableScheduling
public class GroupServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }
}
