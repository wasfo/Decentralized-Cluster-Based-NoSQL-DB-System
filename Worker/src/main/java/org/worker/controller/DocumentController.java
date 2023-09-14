package org.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.worker.api.event.AddDocumentEvent;
import org.worker.api.event.DeleteAllDocumentsEvent;
import org.worker.api.event.DeleteDocumentEvent;
import org.worker.api.writeRequests.AddDocumentRequest;
import org.worker.api.writeRequests.DeleteAllDocumentsRequest;
import org.worker.api.writeRequests.DeleteDocumentRequest;
import org.worker.broadcast.BroadcastService;
import org.worker.broadcast.Topic;
import org.worker.services.CollectionService;
import org.worker.services.DocumentService;
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
        DeleteDocumentEvent event = new DeleteDocumentEvent(username, request);
        broadcastService.broadCastWithKafka(Topic.Delete_Document_Topic, event);

        return DbUtils.getResponseEntity("document with id:" +
                request.getDocId() + " deleted successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/all")
    public <T> ResponseEntity<String> deleteDocumentMany(@RequestBody DeleteAllDocumentsRequest<T> request)
            throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //TODO deleteMany method needs some fixes regarding (class casting) probably.
        DeleteAllDocumentsEvent<T> event = new DeleteAllDocumentsEvent<>(username, request);
        broadcastService.broadCastWithKafka(Topic.Delete_All_Documents_Topic, event);

        return DbUtils.getResponseEntity("documents with property:" +
                request.getCriteria() + " deleted successfully", HttpStatus.OK);

    }

    @PostMapping("/add")
    public ResponseEntity<String> addDocument(@RequestBody @Validated AddDocumentRequest request,
                                              BindingResult bindingResult) throws ExecutionException {

        if (bindingResult.hasErrors()) {
            return DbUtils.getResponseEntity("incorrect fields at adding document request",
                    HttpStatus.BAD_REQUEST);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AddDocumentEvent event = new AddDocumentEvent(username, request);
        broadcastService.broadCastWithKafka(Topic.Add_Document_Topic, event);

        return DbUtils.getResponseEntity("document created successfully",
                HttpStatus.CREATED);


    }

}
