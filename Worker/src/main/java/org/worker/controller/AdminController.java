package org.worker.controller;


import org.springframework.web.bind.annotation.*;
import org.worker.api.writeRequests.DeleteCollectionRequest;

/**
 * this class will contain all admin privileges
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @PostMapping("/hello")
    public String get(@RequestBody DeleteCollectionRequest apiRequest) {
        return "hello my boi";
    }

}
