package org.worker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.worker.api.event.RegistrationEvent;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final UsersRepoService usersRepoService;
    private final BroadcastService broadcastService;

    @Value("${node.name}")
    private String nodeName;

    @Autowired
    public RegistrationController(UsersRepoService usersRepoService, BroadcastService broadcastService) {
        this.usersRepoService = usersRepoService;
        this.broadcastService = broadcastService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        RegistrationEvent registrationEvent = new RegistrationEvent(user);
        broadcastService.broadCastWithKafka(Topic.Register_User_Topic, registrationEvent);

        return new ResponseEntity<>(user.getUsername() + " registered? ", HttpStatus.CREATED);
    }
}
