package org.worker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.worker.api.event.IndexEvent;
import org.worker.api.writeRequests.IndexRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.deserializers.IndexObject;
import org.worker.services.IndexingService;
import org.worker.utils.DbUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.worker.broadcast.Topic.Create_Index_Topic;

@RestController
@RequestMapping("/api/index")
public class IndexingController {

    private final IndexingService indexingService;

    private final HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;

    private final BroadcastService broadcastService;

    @Autowired
    public IndexingController(IndexingService indexingService,
                              HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap, BroadcastService broadcastService) {
        this.indexingService = indexingService;
        this.usersIndexesMap = usersIndexesMap;
        this.broadcastService = broadcastService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createIndex(@RequestBody IndexRequest request) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        IndexEvent event = new IndexEvent(username, request);
        broadcastService.broadCastWithKafka(Topic.Create_Index_Topic, event);

        return DbUtils.getResponseEntity("created index with fieldName:"
                + request.getFieldName(), HttpStatus.CREATED);
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
