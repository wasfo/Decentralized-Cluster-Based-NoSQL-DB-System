package org.worker.broadcast;


import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.worker.api.event.*;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.services.CollectionService;
import org.worker.services.DatabaseService;
import org.worker.services.DocumentService;
import org.worker.services.Implementation.RegistrationService;
import org.worker.services.IndexingService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaListeners {

    private final DatabaseService databaseService;
    private final IndexingService indexingService;
    private final DocumentService documentService;
    private final CollectionService collectionService;
    private final RegistrationService registrationService;


    @Autowired
    public KafkaListeners(DatabaseService databaseService,
                          IndexingService indexingService,
                          DocumentService documentService,
                          CollectionService collectionService,
                          RegistrationService registrationService) {

        this.databaseService = databaseService;
        this.indexingService = indexingService;
        this.documentService = documentService;
        this.collectionService = collectionService;
        this.registrationService = registrationService;
    }


    @KafkaListener(topics = "registerUserTopic")
    public void createIndex(RegistrationEvent event) throws IOException {
        registrationService.registerUser(event.getUser());
    }
    @KafkaListener(topics = "registerUserTopic")
    public void registerUser(RegistrationEvent event) throws IOException {
        registrationService.registerUser(event.getUser());
    }

    @KafkaListener(topics = "createDatabaseTopic")
    public void createDatabase(CreateDatabaseEvent event) {
        databaseService.createDatabase(event.getUsername(), event.getRequest().getDbName());
    }

    @KafkaListener(topics = "deleteDatabaseTopic")
    public void deleteDatabase(DeleteDatabaseEvent event) {
        databaseService.deleteDatabase(event.getUsername(), event.getRequest().getDbName());
    }

    @KafkaListener(topics = "addDocumentTopic")
    public void addDocument(AddDocumentEvent event) throws IOException,
            ExecutionException, InterruptedException,
            ProcessingException {
        documentService.addDocumentToCollection(event.getUsername(),
                event.getRequest().getDbName(),
                event.getRequest().getCollectionName(),
                event.getRequest().getObjectNode());
    }

    @KafkaListener(topics = "deleteCollectionTopic")
    public void deleteCollection(DeleteCollectionEvent event) {
        collectionService.deleteCollection(event.getUsername(),
                event.getRequest().getDbName(),
                event.getRequest().getCollectionName());
    }

    @KafkaListener(topics = "deleteDocumentTopic")
    public void deleteDocument(DeleteDocumentEvent event) throws IOException {
        documentService.deleteById(event.getRequest().getDocId(), event.getUsername(),
                event.getRequest().getDbName(), event.getRequest().getCollectionName());

    }

    @KafkaListener(topics = "deleteAllDocumentsTopic")
    public <T> void deleteAllDocuments(DeleteAllDocumentsEvent<T> event) throws IOException {
        documentService.deleteMany(event.getRequest().getCriteria(), event.getUsername(),
                event.getRequest().getDbName(), event.getRequest().getCollectionName());
    }

    @KafkaListener(topics = "newCollectionTopic")
    public void newCollection(NewCollectionEvent event) throws IOException {
        collectionService.writeCollection(event.getRequest().getSchema(),
                event.getUsername(),
                event.getRequest().getDbName(),
                event.getRequest().getCollection());
    }


    @KafkaListener(topics = "newEmptyCollectionTopic")
    public void newEmptyCollection(NewEmptyCollectionEvent event) throws IOException {
        collectionService.createNewEmptyCollection(event.getRequest().getSchema(),
                event.getUsername(),
                event.getRequest().getDbName(),
                event.getRequest().getCollectionName());
    }
}
