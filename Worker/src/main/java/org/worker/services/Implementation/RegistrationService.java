package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.user.User;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class RegistrationService {
    private final UsersRepoService usersRepoService;
    private final String storagePath;

    @Autowired
    public RegistrationService(UsersRepoService usersRepoService,
                               @Qualifier("storagePath") String storagePath) {
        this.usersRepoService = usersRepoService;
        this.storagePath = storagePath;
    }

    public void registerUser(User user) throws IOException {
        boolean isSaved = usersRepoService.save(user);
        if (isSaved) {
            Path directory = Path.of(storagePath, user.getUsername());
            Path indexes = Path.of(storagePath, user.getUsername(), "indexes.json");
            directory.toFile().mkdir();
            indexes.toFile().createNewFile();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(indexes.toFile(), objectMapper.createArrayNode());
        }
    }
}
