package org.worker.repository.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.worker.repository.UsersRepository;
import org.worker.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.worker.constants.FilePaths.USERS_JSON_PATH;


@Service
public class UsersRepoService implements UsersRepository {


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
    public Boolean save(User user) {
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
            InputStream inputStream = Files.newInputStream(Path.of(USERS_JSON_PATH));
            List<User> users = objectMapper.readValue(inputStream, typeReference);
            inputStream.close();

            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    @Override
    public Boolean deleteByEmail(String email) {
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
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(USERS_JSON_PATH).toFile(), users);
    }

    private boolean userExists(String email) {
        List<User> users = readUsers();
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(email));
    }

}
