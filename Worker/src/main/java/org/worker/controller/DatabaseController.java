package org.worker.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.worker.api.event.CreateDatabaseEvent;
import org.worker.api.event.DeleteDatabaseEvent;
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
    public ResponseEntity<String> createDatabase(@RequestBody @NotBlank @NotNull String dbName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ResponseEntity<String> response = databaseService.createDatabase(username, dbName);
        if (DbUtils.isResponseSuccessful(response)) {
            CreateDatabaseEvent event = new CreateDatabaseEvent();
            event.setUsername(username);
            event.setDatabaseName(dbName);
            broadcastService.broadCastWithKafka(Topic.Create_Database_Topic, event);
        }
        return response;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDatabase(@RequestBody @NotBlank @NotNull String dbName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ResponseEntity<String> response = databaseService.deleteDatabase(username, dbName);
        if (DbUtils.isResponseSuccessful(response)) {
            DeleteDatabaseEvent event = new DeleteDatabaseEvent();
            event.setUsername(username);
            event.setDatabaseName(dbName);
            broadcastService.broadCastWithKafka(Topic.Delete_Database_Topic, event);
        }
        return response;
    }

}
