package org.worker.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.worker.datastructure.IndexingMap;
import org.worker.deserializers.IndexObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.repository.UsersRepository;
import org.worker.user.User;

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
    public HashMap<String, IndexingMap> usersIndexMap() {
        HashMap<String, IndexingMap> map = new HashMap<>();
        List<User> users = usersRepository.readUsers();
        for (User user : users) {
            map.put(user.getUsername(), new IndexingMap());
        }
        return map;
    }
}
