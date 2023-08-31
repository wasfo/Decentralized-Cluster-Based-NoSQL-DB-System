package com.org.controller;


import com.org.api.request.RegistrationRequest;
import com.org.loadbalance.RoundRobinLoadBalancer;
import com.org.node.Node;
import com.org.service.RegistrationService;
import com.org.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    private RegistrationService registrationService;
    private RoundRobinLoadBalancer loadBalancer;

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  RoundRobinLoadBalancer loadBalancer) {
        this.registrationService = registrationService;
        this.loadBalancer = loadBalancer;
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody User user) {

        boolean isRegisteredSuccessfully = registrationService.registerUser(user.getUsername(), user.getPassword());

        if (isRegisteredSuccessfully) {
            registrationService.registerUserInNodes(user);
            Node node = loadBalancer.getNextNode();
            return ResponseEntity.ok(node.toString());
        }
        return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
