package org.worker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.worker.api.IndexRequest;
import org.worker.deserializers.IndexObject;
import org.worker.services.IndexingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/index")
public class IndexingController {

    private IndexingService indexingService;

    private HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;

    @Autowired
    public IndexingController(IndexingService indexingService,
                              HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap) {
        this.indexingService = indexingService;
        this.usersIndexesMap = usersIndexesMap;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createIndex(@RequestBody IndexRequest request) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return indexingService.createIndex(username,
                request.getDbName(),
                request.getCollectionName(),
                request.getFieldName());
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody IndexRequest request) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        indexingService.removeIndex(username,
                request.getDbName(),
                request.getCollectionName(),
                request.getFieldName());

        return new ResponseEntity<>(usersIndexesMap, HttpStatus.OK);
    }
}
