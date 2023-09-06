package org.worker.constants;

import java.nio.file.Path;

public class FilePaths {

    public static String USERS_JSON_PATH = Path.of(
            System.getProperty("user.dir"),
            "app",
            "Storage",
            System.getenv("node.name") + "-Storage/users.json").toString();

    public static String Storage_Path = Path.of(
            System.getProperty("user.dir"),
            "app",
            "Storage",
            System.getenv("node.name") + "-Storage").toString();

}
