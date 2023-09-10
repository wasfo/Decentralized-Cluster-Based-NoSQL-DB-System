package org.worker.controller;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.worker.api.event.AddDocumentEvent;
import org.worker.api.event.DeleteAllDocumentsEvent;
import org.worker.api.event.DeleteDocumentEvent;
import org.worker.api.writeRequests.AddDocumentRequest;
import org.worker.api.writeRequests.DeleteAllDocumentsRequest;
import org.worker.api.writeRequests.DeleteDocumentRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.models.JsonProperty;
import org.worker.services.CollectionService;
import org.worker.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.worker.utils.DbUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CollectionService collectionService;
    private final BroadcastService broadcastService;


    @Autowired
    public DocumentController(DocumentService documentService,
                              CollectionService collectionService,
                              BroadcastService broadcastService) {

        this.documentService = documentService;
        this.collectionService = collectionService;
        this.broadcastService = broadcastService;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDocumentById(@RequestBody DeleteDocumentRequest request)
            throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = documentService.deleteById(request.getDocId(),
                username,
                request.getDbName(),
                request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            DeleteDocumentEvent event = new DeleteDocumentEvent();
            event.setBroadcastingNodeName(broadcastService.nodeName);
            event.setUsername(username);
            event.setDbName(request.getDbName());
            event.setCollectionName(request.getCollectionName());
            event.setDocumentId(request.getDocId());
            broadcastService.broadCastWithKafka(Topic.Delete_Document_Topic, event);
        }

        return response;
    }

    @DeleteMapping("/delete/all")
    public <T> ResponseEntity<String> deleteDocumentMany(@RequestBody DeleteAllDocumentsRequest<T> request)
            throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = documentService.deleteMany(request.getCriteria(),
                username,
                request.getDbName(),
                request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            DeleteAllDocumentsEvent<T> event = new DeleteAllDocumentsEvent<>();
            event.setCriteria(request.getCriteria());
            event.setUsername(username);
            event.setDbName(request.getDbName());
            event.setCollectionName(request.getCollectionName());
            broadcastService.broadCastWithKafka(Topic.Delete_All_Documents_Topic, event);
        }

        return response;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDocument(@RequestBody @Validated AddDocumentRequest request,
                                              BindingResult bindingResult) throws ExecutionException {

        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect fields at adding document request",
                    HttpStatus.BAD_REQUEST);
        }
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            ResponseEntity<String> response = collectionService.addDocument(username, request.getDbName(),
                    request.getCollectionName(), request.getObjectNode());

            if (DbUtils.isResponseSuccessful(response)) {
                AddDocumentEvent event = new AddDocumentEvent();
                event.setBroadcastingNodeName(broadcastService.nodeName);
                event.setUsername(username);
                event.setDbName(request.getDbName());
                event.setCollectionName(request.getCollectionName());
                event.setObjectNode(request.getObjectNode());
            }
            return response;

        } catch (IOException | ProcessingException | InterruptedException e) {
            return DbUtils.getResponseEntity("something went" +
                    " wrong adding new document", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
