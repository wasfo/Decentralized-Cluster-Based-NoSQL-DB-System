package org.com;


import lombok.extern.slf4j.Slf4j;
import org.com.models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;


@SpringBootApplication
@Slf4j
public class LoadBalancerApp {

    public static void main(String[] args) {
        SpringApplication.run((LoadBalancerApp.class), args);
    }

}