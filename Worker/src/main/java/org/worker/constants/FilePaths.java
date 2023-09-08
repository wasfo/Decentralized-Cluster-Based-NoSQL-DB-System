package org.worker.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class FilePaths {
    @Value("${node.port}")
    private static String nodeName;

    public static String USERS_JSON_PATH = Path.of(
            System.getProperty("user.dir"),
            "Storage",
            nodeName + "-Storage/users.json").toString();

    public static String Storage_Path = Path.of(
            System.getProperty("user.dir"),
            "Storage",
            "Node1" + "-Storage").toString();


    public static void main(String[] args) {
    }
}
