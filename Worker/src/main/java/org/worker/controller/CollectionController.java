package org.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.worker.api.event.DeleteCollectionEvent;
import org.worker.api.event.NewCollectionEvent;
import org.worker.api.event.NewEmptyCollectionEvent;
import org.worker.api.readRequests.ReadCollectionRequest;
import org.worker.api.writeRequests.DeleteCollectionRequest;
import org.worker.api.writeRequests.NewCollectionRequest;
import org.worker.api.writeRequests.NewEmptyCollectionRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.models.Collection;
import org.worker.services.CollectionService;
import org.worker.utils.DbUtils;

import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private CollectionService collectionService;
    private BroadcastService broadcastService;

    @Value("${node.name}")
    private String nodeName;

    @Autowired
    public CollectionController(CollectionService collectionService,
                                BroadcastService broadcastService) {
        this.collectionService = collectionService;
        this.broadcastService = broadcastService;
    }

    @GetMapping("/read")
    public ResponseEntity<?> readCollection(@RequestBody ReadCollectionRequest request) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Collection> collection = collectionService.readCollection(username,
                request.getDbName(),
                request.getCollectionName());

        if (collection.isPresent())
            return new ResponseEntity<>(collection.get(), HttpStatus.FOUND);
        return new ResponseEntity<>("Collection doesn't not exit", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewCollection(@RequestBody @Validated NewCollectionRequest request,
                                                      BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect new collection request",
                    HttpStatus.BAD_REQUEST);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = collectionService.writeCollection(request.getSchema(),
                username, request.getDbName(), request.getCollection());

        if (DbUtils.isResponseSuccessful(response)) {
            NewCollectionEvent event = new NewCollectionEvent();
            event.setCollection(request.getCollection());
            event.setUsername(username);
            event.setSchema(request.getSchema());
            event.setDbName(request.getDbName());
            event.setBroadcastingNodeName(nodeName);
            broadcastService.broadCastWithKafka(Topic.New_Empty_Collection_Topic, event);
        }

        return response;
    }

    @PostMapping("/newEmpty")
    public ResponseEntity<String> createNewEmptyCollection(@RequestBody NewEmptyCollectionRequest request) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = collectionService.createNewEmptyCollection(request.getSchema(),
                username, request.getDbName(), request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            NewEmptyCollectionEvent event = new NewEmptyCollectionEvent();
            event.setCollectionName(request.getCollectionName());
            event.setUsername(username);
            event.setSchema(request.getSchema());
            event.setDbName(request.getDbName());
            event.setBroadcastingNodeName(nodeName);
            event.setUsername(username);
            broadcastService.broadCastWithKafka(Topic.New_Empty_Collection_Topic, event);
        }

        return response;
    }


    @PostMapping("/delete")
    public ResponseEntity<String> deleteCollection(@RequestBody DeleteCollectionRequest request) {


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("USERNAME  IS -> " + username);
        ResponseEntity<String> response = collectionService.deleteCollection(username,
                request.getDbName(), request.getCollectionName());
        if (DbUtils.isResponseSuccessful(response)) {
            DeleteCollectionEvent event = new DeleteCollectionEvent();
            event.setBroadcastingNodeName(nodeName);
            event.setUsername(username);
            event.setDbName(request.getDbName());
            event.setCollectionName(request.getCollectionName());
            broadcastService.broadCastWithKafka(Topic.Delete_Collection_Topic, event);
        }
        return response;

    }


}
