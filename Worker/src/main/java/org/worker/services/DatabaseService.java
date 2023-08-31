package org.worker.services;


import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DatabaseService {
    public List<String> showDatabases(String username);

    public ResponseEntity<String> createDatabase(String username, String dbName);

    public ResponseEntity<String> deleteDatabase(String username, String dbName);

}
