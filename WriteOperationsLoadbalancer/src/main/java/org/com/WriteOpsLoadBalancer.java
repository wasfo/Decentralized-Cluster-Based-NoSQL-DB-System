package org.com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication

public class WriteOpsLoadBalancer {
    public static void main(String[] args) {
        SpringApplication.run((WriteOpsLoadBalancer.class), args);
    }
}