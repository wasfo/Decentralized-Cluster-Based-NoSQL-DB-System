package org.worker.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.worker.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/databases")
public class DatabaseController {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping
    public List<String> showDatabases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "ahmad2@gmail.com";
        return databaseService.showDatabases(username);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDatabase(@RequestBody @NotBlank @NotNull String dbName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "ahmad2@gmail.com";
        return databaseService.createDatabase(username, dbName);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteDatabase(@RequestBody @NotBlank @NotNull String dbName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return databaseService.deleteDatabase(authentication.getName(), dbName);
    }

}
