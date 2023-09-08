package org.worker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/connection")
public class ConnectionController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/get")
    public ResponseEntity<String> getConnection(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(serverPort);
    }
}
