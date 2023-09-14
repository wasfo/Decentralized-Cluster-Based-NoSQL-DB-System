package org.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.worker.services.IndexingService;


@RestController
@RequestMapping("/connection")
public class ConnectionController {

    private final IndexingService indexingService;
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public ConnectionController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @GetMapping("/get")
    public ResponseEntity<String> getConnection() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        indexingService.loadAllIndexesToMap(username);
        return ResponseEntity.ok(serverPort);
    }
}
