package org.worker;

import com.netflix.discovery.EurekaClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;

import java.io.File;


@SpringBootApplication(exclude = {EurekaClientAutoConfiguration.class,
        EurekaDiscoveryClientConfiguration.class})
//exclude = SecurityAutoConfiguration.class
public class NoSqlDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoSqlDbApplication.class, args);
        createStorageFileIfNotExist();
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
