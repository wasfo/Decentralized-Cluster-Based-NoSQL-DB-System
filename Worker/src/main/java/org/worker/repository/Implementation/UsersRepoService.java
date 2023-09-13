package org.worker.repository.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.worker.constants.FilePaths;
import org.worker.repository.UsersRepository;
import org.worker.user.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@DependsOn("filePaths")
public class UsersRepoService implements UsersRepository {
    private String usersPath;

    @Autowired
    public UsersRepoService(@Qualifier("usersPath") String usersPath) {
        this.usersPath = usersPath;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<List<User>> typeReference = new TypeReference<>() {
    };

    public UsersRepoService() {
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> users = readUsers();
        Optional<User> targetUser = users
                .stream()
                .filter(user -> user.getUsername().equals(email))
                .findFirst();
        return targetUser;
    }

    @Override
    public synchronized Boolean save(User user) {
        try {
            if (userExists(user.getUsername()))
                return false;
            else {
                List<User> existingUsers = readUsers();
                existingUsers.add(user);
                writeUsersToJson(existingUsers);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> readUsers() {
        try {
            File file = new File(usersPath);
            List<User> users = objectMapper.readValue(file, typeReference);
            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    @Override
    public synchronized Boolean deleteByEmail(String email) {
        List<User> users = readUsers();
        Optional<User> targetUser = users
                .parallelStream()
                .filter(user -> user.getUsername().equals(email))
                .findFirst();

        targetUser.ifPresent(users::remove);
        try {
            writeUsersToJson(users);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private synchronized void writeUsersToJson(List<User> users) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(usersPath).toFile(), users);
    }

    private boolean userExists(String email) {
        List<User> users = readUsers();
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(email));
    }

}
