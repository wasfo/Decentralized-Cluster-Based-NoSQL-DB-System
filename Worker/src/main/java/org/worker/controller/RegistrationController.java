package org.worker.controller;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private UsersRepoService usersRepoService;
    @Autowired
    public RegistrationController(UsersRepoService usersRepoService) {
        this.usersRepoService = usersRepoService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        boolean isRegistered = usersRepoService.save(user);
        return new ResponseEntity<>(user.getUsername() + " registered? " +
                isRegistered, HttpStatus.OK);
    }
}
