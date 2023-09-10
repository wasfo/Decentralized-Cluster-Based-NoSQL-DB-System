package org.worker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.worker.api.event.DeleteAllDocumentsEvent;
import org.worker.api.event.DeleteDocumentEvent;
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

@Controller
@RequestMapping("/api/documents")
public class DocumentController {

    private DocumentService documentService;
    private CollectionService collectionService;
    private BroadcastService broadcastService;


    @Autowired
    public DocumentController(DocumentService documentService,
                              CollectionService collectionService,
                              BroadcastService broadcastService) {

        this.documentService = documentService;
        this.collectionService = collectionService;
        this.broadcastService = broadcastService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable String id,
                                                     @RequestBody DeleteDocumentRequest request)
            throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = documentService.deleteById(id,
                username,
                request.getDbName(),
                request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            DeleteDocumentEvent deleteDocumentEvent = new DeleteDocumentEvent();
            deleteDocumentEvent.setUsername(username);
            deleteDocumentEvent.setDbName(request.getDbName());
            deleteDocumentEvent.setCollectionName(request.getCollectionName());
            deleteDocumentEvent.setDocumentId(id);
            broadcastService.broadCastWithKafka(Topic.Delete_Document_Topic, deleteDocumentEvent);
        }

        return response;
    }

    @DeleteMapping
    public <T> ResponseEntity<String> deleteDocumentMany(@RequestBody DeleteAllDocumentsRequest<T> request)
            throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<String> response = documentService.deleteMany(request.getCriteria(),
                username,
                request.getDbName(),
                request.getCollectionName());

        if (DbUtils.isResponseSuccessful(response)) {
            DeleteAllDocumentsEvent<T> deleteDocumentEvent = new DeleteAllDocumentsEvent<>();
            deleteDocumentEvent.setCriteria(request.getCriteria());
            deleteDocumentEvent.setUsername(username);
            deleteDocumentEvent.setDbName(request.getDbName());
            deleteDocumentEvent.setCollectionName(request.getCollectionName());
            broadcastService.broadCastWithKafka(Topic.Delete_All_Documents_Topic, deleteDocumentEvent);
        }

        return response;
    }

}
