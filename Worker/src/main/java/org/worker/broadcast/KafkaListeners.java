package org.worker.broadcast;


import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.worker.api.event.*;
import org.worker.services.CollectionService;
import org.worker.services.DatabaseService;
import org.worker.services.DocumentService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaListeners {

    private final DatabaseService databaseService;
    private DocumentService documentService;
    private final CollectionService collectionService;

    @Autowired
    public KafkaListeners(DatabaseService databaseService,
                          DocumentService documentService,
                          CollectionService collectionService) {
        this.databaseService = databaseService;
        this.documentService = documentService;
        this.collectionService = collectionService;
    }

    @KafkaListener(topics = "createDatabaseTopic")
    public void createDatabase(CreateDatabaseEvent event) {
        if (event.isBroadcasted()) {
            databaseService.createDatabase(event.getUsername(), event.getDatabaseName());
        }
    }

    @KafkaListener(topics = "deleteDatabaseTopic")
    public void deleteDatabase(DeleteDatabaseEvent event) {
        databaseService.deleteDatabase(event.getUsername(), event.getDatabaseName());
    }

    @KafkaListener(topics = "addDocumentTopic")
    public void addDocument(AddDocumentEvent event) throws IOException, ExecutionException, InterruptedException, ProcessingException {
        collectionService.addDocument(event.getUsername(),
                event.getDbName(),
                event.getCollectionName(),
                event.getObjectNode());
    }

    @KafkaListener(topics = "deleteCollectionTopic")
    public void deleteCollection(DeleteCollectionEvent event) {
        collectionService.deleteCollection(event.getUsername(),
                event.getDbName(),
                event.getCollectionName());
    }

    @KafkaListener(topics = "deleteDocumentTopic")
    public void deleteDocument(DeleteDocumentEvent event) {

    }

    @KafkaListener(topics = "newCollectionTopic")
    public void newCollection(NewCollectionEvent event) throws IOException {
        collectionService.writeCollection(event.getSchema(),
                event.getUsername(),
                event.getDbName(),
                event.getCollection());
    }

    @KafkaListener(topics = "newEmptyCollectionTopic")
    public void newEmptyCollection(NewEmptyCollectionEvent event) throws IOException {
        collectionService.createNewEmptyCollection(event.getSchema(),
                event.getUsername(),
                event.getDbName(),
                event.getCollectionName());
    }
}
