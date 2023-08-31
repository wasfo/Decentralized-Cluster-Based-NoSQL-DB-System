package org.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.io.File;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class NoSqlDbApplication {

    public static void main(String[] args) {

        SpringApplication.run(NoSqlDbApplication.class, args);

        createStorageFileIfNotExist();
    }

    static public void createStorageFileIfNotExist() {
        String root = System.getProperty("user.dir");
        String directory = "\\Storage";
        File theDir = new File(root + directory);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
    }
}
