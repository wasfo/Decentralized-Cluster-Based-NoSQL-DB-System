package org.worker.configs;


import org.worker.deserializers.IndexObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

@Configuration
public class UsersIndexMapConfig {

    /**
     *
     * usersIndexMap bean will contain the username
     * and the corresponding map that contains the indexes
     */
    @Bean
    public HashMap<String, HashMap<IndexObject, List<String>>> usersIndexMap() {
        return new HashMap<>();
    }
}
