package org.worker.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.worker.api.event.CreateDatabaseEvent;
import org.worker.api.event.DeleteDatabaseEvent;
import org.worker.api.writeRequests.CreateDatabaseRequest;
import org.worker.api.writeRequests.DeleteDatabaseRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.worker.utils.DbUtils;

import java.util.List;

import static org.worker.utils.DbUtils.getResponseEntity;

@RestController
@RequestMapping("/api/databases")
public class DatabaseController {
    private final DatabaseService databaseService;
    private final BroadcastService broadcastService;


    @Autowired
    public DatabaseController(DatabaseService databaseService, BroadcastService broadcastService) {
        this.databaseService = databaseService;
        this.broadcastService = broadcastService;
    }


    @GetMapping
    public List<String> showDatabases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return databaseService.showDatabases(username);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDatabase(@RequestBody CreateDatabaseRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CreateDatabaseEvent event = new CreateDatabaseEvent(username, request);
        broadcastService.broadCastWithKafka(Topic.Create_Database_Topic, event);
        return getResponseEntity("Database Created Successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDatabase(@RequestBody DeleteDatabaseRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        DeleteDatabaseEvent event = new DeleteDatabaseEvent(username, request);
        broadcastService.broadCastWithKafka(Topic.Delete_Database_Topic, event);
        return getResponseEntity("Database deleted Successfully", HttpStatus.OK);
    }

}
