package com.org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.org.user.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UsersService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeFactory typeFactory = objectMapper.getTypeFactory();
    private static final CollectionType collectionType = typeFactory.constructCollectionType(List.class, User.class);

    private static final File usersFile = new File("C:\\Users\\super\\Desktop\\Decentralizd Nosql DB\\" +
            "BootstrappingNode\\BootstrappingNode\\BootstrappingNode\\src\\main\\java\\com\\org\\user\\uesrs.json");

    public List<User> getUsers() throws IOException {
        List<User> users = objectMapper.readValue(usersFile, collectionType);
        return users;
    }

    public boolean saveUser(User newUser) {
        try {
            List<User> users = getUsers();
            users.add(newUser);
            objectMapper.writeValue(usersFile, users);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
