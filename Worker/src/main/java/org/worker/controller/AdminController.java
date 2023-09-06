package org.worker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.worker.api.writeRequests.DeleteCollectionRequest;
import org.worker.models.Collection;
import org.worker.services.CollectionService;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * this class will contain all admin privileges
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CollectionService collectionService;

    @GetMapping("/unprotected")
    public String get()  {
        return "hello from unprotected";
    }

    @GetMapping("/protected")
    public String getpro()  {
        return "hello from protected my boi";
    }

}
