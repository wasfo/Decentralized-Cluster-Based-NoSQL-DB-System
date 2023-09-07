package org.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.worker.user.User;

import java.io.File;


@SpringBootApplication
@Slf4j
//exclude = {EurekaClientAutoConfiguration.class,
//        EurekaDiscoveryClientConfiguration.class}
//exclude = SecurityAutoConfiguration.class
public class NoSqlDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoSqlDbApplication.class, args);
        //createStorageFileIfNotExist();
    }

    @KafkaListener(topics = "UserTopic")
    public void receiveFromKafka(User user) {
        log.info("recieved from kafka this user - {}", user);
    }


    static public void createStorageFileIfNotExist() {
        String root = System.getProperty("user.dir");
        String nodeName = System.getenv("node.name") + "_Storage";
        String directory = "\\" + nodeName;
        File theDir = new File(root + directory);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
    }
}
