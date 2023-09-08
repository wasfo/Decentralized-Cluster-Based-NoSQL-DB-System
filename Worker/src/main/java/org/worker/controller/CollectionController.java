package org.worker.controller;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.worker.api.readRequests.ReadCollectionRequest;
import org.worker.api.writeRequests.AddDocumentRequest;
import org.worker.api.writeRequests.DeleteCollectionRequest;
import org.worker.api.writeRequests.NewCollectionRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.models.Collection;
import org.worker.services.CollectionService;
import org.worker.user.UserCredentials;
import org.worker.utils.DbUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.worker.constants.FilePaths.Storage_Path;


@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private CollectionService collectionService;
    private BroadcastService broadcastService;

    @Autowired
    public CollectionController(CollectionService collectionService,
                                BroadcastService broadcastService) {
        this.collectionService = collectionService;
        this.broadcastService = broadcastService;
    }

    @GetMapping("/read")
    public ResponseEntity<?> readCollection(@RequestBody ReadCollectionRequest request) throws IOException, ExecutionException, InterruptedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Collection> collection = collectionService.readCollection(username,
                request.getDbName(),
                request.getCollectionName());

        if (collection.isPresent())
            return new ResponseEntity<>(collection.get(), HttpStatus.FOUND);
        return new ResponseEntity<>("Collection doesn't not exit", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewCollection(@RequestBody @Validated NewCollectionRequest request,
                                                      @RequestHeader HttpHeaders headers,
                                                      BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect new collection request",
                    HttpStatus.BAD_REQUEST);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = collectionService.writeCollection(request.getSchema(),
                username, request.getDbName(), request.getCollection());

        if (DbUtils.isResponseSuccessful(response)) {
            if (!request.isBroadcasted()) {
                broadcastService.broadCast(request, headers, "/api/collections/new",
                        HttpMethod.POST);
            }
        }

        return response;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCollection(@RequestBody DeleteCollectionRequest request,
                                                   @RequestHeader HttpHeaders headers) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ResponseEntity<String> response = collectionService.deleteCollection(username,
                request.getDbName(), request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            if (!request.isBroadcasted()) {
                broadcastService.broadCast(request,
                        headers,
                        "/api/collections/new",
                        HttpMethod.DELETE);
            }
        }
        return response;

    }

    @PostMapping("/add/doc")
    public ResponseEntity<String> addDocument(@RequestBody @Validated AddDocumentRequest request,
                                              @RequestHeader HttpHeaders headers,
                                              BindingResult bindingResult) throws  ExecutionException {

        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect fields at adding document request",
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            ResponseEntity<String> response = collectionService.addDocument(username, request.getDbName(),
                    request.getCollectionName(), request.getObjectNode());


            if (DbUtils.isResponseSuccessful(response)) {
                if (!request.isBroadcasted()) {
                    broadcastService.broadCast(request,
                            headers,
                            "/api/collections/add/doc",
                            HttpMethod.POST);
                }
            }
            return response;

        } catch (IOException | ProcessingException | InterruptedException e) {
            return DbUtils.getResponseEntity("something went" +
                    " wrong adding new document", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
