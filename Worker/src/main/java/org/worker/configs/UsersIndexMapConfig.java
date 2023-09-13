package org.worker.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.worker.deserializers.IndexObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.worker.repository.UsersRepository;
import java.util.HashMap;
import java.util.List;

@Configuration
@DependsOn("usersRepoService")
public class UsersIndexMapConfig {
    private final UsersRepository usersRepository;

    @Autowired
    public UsersIndexMapConfig(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * usersIndexMap bean will contain the username
     * and the corresponding map that contains the indexes
     */
    @Bean
    public HashMap<String, HashMap<IndexObject, List<String>>> usersIndexMap() {
        return new HashMap<>();
    }
}
