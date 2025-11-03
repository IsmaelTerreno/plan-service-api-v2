package com.remotejob.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = {"com.remotejob.jobservice.entity"})
@EnableJpaRepositories(basePackages = {"com.remotejob.jobservice.repository"})
public class PlanServiceAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanServiceAPIApplication.class, args);
    }

}


