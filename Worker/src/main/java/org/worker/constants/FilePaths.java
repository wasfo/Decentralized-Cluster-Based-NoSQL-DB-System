package org.worker.constants;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Configuration
public class FilePaths {

    @Value("${node.name}")
    public String nodeName;

    @Bean(name = "usersPath")
    public String getUSERS_JSON_PATH() {
        return Path.of(
                "Storage",
                nodeName + "-Storage/users.json").toString();
    }

    @Bean(name = "storagePath")
    public String getStorage_Path() {
        return Path.of(
                "Storage",
                nodeName + "-Storage").toString();
    }


}
