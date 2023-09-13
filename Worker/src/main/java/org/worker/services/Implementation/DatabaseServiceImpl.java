package org.worker.services.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.worker.constants.FilePaths;
import org.worker.services.DatabaseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.worker.utils.DbUtils.getResponseEntity;


@Service
@Primary
public class DatabaseServiceImpl implements DatabaseService {


    private String storagePath;

    public DatabaseServiceImpl(@Qualifier("storagePath") String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public List<String> showDatabases(String username) {
        File directory = new File(storagePath + "//" + username);
        if (!directory.exists())
            return Collections.emptyList();

        List<File> files = List.of(Objects.requireNonNull(directory.listFiles()));
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    @Override
    public ResponseEntity<String> createDatabase(String username, String dbName) {
        File userFile = Path.of(storagePath, username).toFile();

        if (userFile.exists()) {
            try {
                File newDirectory = new File(userFile, dbName);
                boolean isDirectoryCreated = newDirectory.mkdir();
                if (isDirectoryCreated) {
                    String responseMessage = "Database '" + dbName + "' created successfully.";
                    return getResponseEntity(responseMessage, HttpStatus.CREATED);
                }
                return getResponseEntity("something went wrong creating database", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                String errorMessage = "Failed to create database.";
                return getResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return getResponseEntity("user does not exist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDatabase(String username, String dbName) {
        File userFile = Path.of(storagePath, username).toFile();
        if (userFile.exists()) {
            try {
                File targetDbName = new File(userFile + "//" + dbName);
                if (targetDbName.exists()) {
                    FileUtils.deleteDirectory(targetDbName);
                    return getResponseEntity("Database deleted Successfully", HttpStatus.OK);
                }
                return getResponseEntity("Database does not exit", HttpStatus.OK);

            } catch (Exception e) {
                return getResponseEntity("failed to delete database", HttpStatus.BAD_REQUEST);
            }
        }
        return getResponseEntity("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
