package org.worker.controller;


import org.springframework.web.bind.annotation.*;
import org.worker.api.writeRequests.DeleteCollectionRequest;

/**
 * this class will contain all admin privileges
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/hello")
    public String get() {
        System.out.println("hello my boiiiii!");
        return "hello my boi";
    }

}
