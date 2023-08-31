package org.worker.controller;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.worker.api.DeleteCollectionRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.models.Collection;
import org.worker.api.AddDocumentRequest;
import org.worker.api.NewCollectionRequest;
import org.worker.services.CollectionService;
import org.worker.user.UserCredentials;
import org.worker.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Path;


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

    @PostMapping("/read")
    public Collection readCollection(@RequestBody Path collectionDir) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return collectionService.readCollection(collectionDir).get();
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewCollection(@RequestBody @Validated NewCollectionRequest request,
                                                      BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect new collection request",
                    HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        ResponseEntity<String> response = collectionService.writeCollection(request.getSchema(),
                username, request.getDbName(), request.getCollection());

        if (DbUtils.isResponseSuccessful(response)) {
            if (!request.isBroadcasted()) {

                UserCredentials credentials = new UserCredentials(username, password);
                request.setBroadcasted(true);
                broadcastService.broadCast(request, credentials, "/api/collections/new");
            }
        }

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            return collectionService.addDocument(username, request.getDbName(),
                    request.getCollectionName(), request.getDocument());
        } catch (IOException | ProcessingException e) {
            return DbUtils.getResponseEntity("something went" +
                    " wrong adding new document", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
