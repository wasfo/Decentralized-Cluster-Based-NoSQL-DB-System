package org.worker.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.worker.api.APIRequest;
import org.worker.api.DeleteCollectionRequest;

/**
 * this class will contain all admin privileges
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @PostMapping("/hello")
    public String get(@RequestBody DeleteCollectionRequest apiRequest) {
        System.out.println(apiRequest.getUserCredentials());
        return "hello my boi";
    }

}
