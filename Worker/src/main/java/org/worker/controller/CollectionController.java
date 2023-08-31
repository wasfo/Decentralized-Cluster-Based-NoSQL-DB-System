package org.worker.controller;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> readCollection(@RequestBody ReadCollectionRequest request) throws IOException {
        // String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String username = "ahmad2@gmail.com";
        Path path = Path.of(Storage_Path, username, request.getDbName(), request.getCollectionName());

        Optional<Collection> collection = collectionService.readCollection(path);
        if (collection.isPresent())
            return new ResponseEntity<>(collection.get(), HttpStatus.OK);
        return new ResponseEntity<>("Collection doesn't not exit", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewCollection(@RequestBody @Validated NewCollectionRequest request,
                                                      BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect new collection request",
                    HttpStatus.BAD_REQUEST);
        }

        String username = "ahmad2@gmail.com";

        ResponseEntity<String> response = collectionService.writeCollection(request.getSchema(),
                username, request.getDbName(), request.getCollection());

//        if (DbUtils.isResponseSuccessful(response)) {
//            if (!request.isBroadcasted()) {
//
//                UserCredentials credentials = new UserCredentials(username, password);
//                request.setBroadcasted(true);
//                broadcastService.broadCast(request, credentials, "/api/collections/new");
//            }
//        }

        return response;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCollection(@RequestBody DeleteCollectionRequest request) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return collectionService.deleteCollection(username, request.getDbName(), request.getCollectionName());
    }

    @PostMapping("/add/doc")
    public ResponseEntity<String> addDocument(@RequestBody @Validated AddDocumentRequest request,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect fields at adding document request",
                    HttpStatus.BAD_REQUEST);
        }
        try {
            //  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //  String username = authentication.getName();
            String username = "ahmad2@gmail.com";
            return collectionService.addDocument(username, request.getDbName(),
                    request.getCollectionName(), request.getObjectNode());
        } catch (IOException | ProcessingException e) {
            return DbUtils.getResponseEntity("something went" +
                    " wrong adding new document", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
