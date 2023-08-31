package com.org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.org.api.request.RegistrationRequest;
import com.org.node.Node;
import com.org.user.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final List<Node> nodes;
    private final RestTemplate restTemplate;
    private final UsersService usersService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    public RegistrationServiceImpl(List<Node> nodes, RestTemplate restTemplate, UsersService usersService) {
        this.nodes = nodes;
        this.restTemplate = restTemplate;
        this.usersService = usersService;
    }

    @Override
    public boolean registerUser(String username, String password) {
        User newUser = User.builder()
                .username(username)
                .password(password)
                .build();
        return usersService.saveUser(newUser);
    }

    @Override
    public ResponseEntity<String> registerUserInNodes(User user) {
        ResponseEntity<String> response = null;
        try {
            for (Node node : nodes) {
                String url = getUrl(node.getHostname(), node.getPort(), "/api/register");
                response = restTemplate.postForEntity(url, url, String.class);
            }
        } catch (Exception e) {
            logger.error("cluster is not running:", e.getMessage());
        }

        return response;
    }

    public boolean userExists(String username) throws IOException {

        List<User> users = usersService.getUsers();
        return users.stream().map(User::getUsername).anyMatch(user -> user.equals(username));
    }

    static public String getUrl(String hostname, int port, String endPoint) {
        return "http://localhost:" + port + "/" + hostname + "/" + endPoint;
    }

    public static void main(String[] args) throws IOException {
        List<Node> nodes = new ArrayList<>();
        RestTemplate restTemplate1 = new RestTemplate();
        UsersService usersService = new UsersService();
        RegistrationServiceImpl registrationService = new RegistrationServiceImpl(nodes, restTemplate1, usersService);

        System.out.println(registrationService.userExists("ahmad"));
    }

}
