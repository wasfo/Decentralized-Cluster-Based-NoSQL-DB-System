package org.worker.services.Implementation;
import org.worker.services.DatabaseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static org.worker.constants.FilePaths.Storage_Path;
import static org.worker.utils.DbUtils.getResponseEntity;
import static org.worker.utils.DbUtils.getUserDir;

@Service
@Primary
public class DatabaseServiceImpl implements DatabaseService {

    @Override
    public List<String> showDatabases(String username) {
        File directory = new File(Storage_Path + "//" + username);
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
        File userFile = getUserDir(username);

        if (userFile.exists()) {
            try {
                File newDirectory = new File(userFile, dbName);
                boolean isDirectoryCreated = newDirectory.mkdir();
                if (isDirectoryCreated) {
                    String responseMessage = "Database '" + dbName + "' created successfully.";
                    return getResponseEntity(responseMessage, HttpStatus.CREATED);
                }
                return getResponseEntity("something went wrong creating database", HttpStatus.CREATED);
            } catch (Exception e) {
                String errorMessage = "Failed to create database.";
                return getResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return getResponseEntity("user does not exist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteDatabase(String username, String dbName) {
        File userFile = getUserDir(username);
        if (userFile.exists()) {
            try {
                File targetDbName = new File(userFile + "//" + dbName);
                if (targetDbName.exists()){
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
