package org.worker.broadcast;


import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.worker.api.event.*;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.services.CollectionService;
import org.worker.services.DatabaseService;
import org.worker.services.DocumentService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaListeners {

    @Value("${node.name}")
    private String nodeName;
    private final DatabaseService databaseService;
    private final DocumentService documentService;
    private final CollectionService collectionService;
    private final UsersRepoService usersRepoService;

    @Autowired
    public KafkaListeners(DatabaseService databaseService,
                          DocumentService documentService,
                          CollectionService collectionService,
                          UsersRepoService usersRepoService) {
        this.databaseService = databaseService;
        this.documentService = documentService;
        this.collectionService = collectionService;
        this.usersRepoService = usersRepoService;
    }
    //registerUserTopic

    @KafkaListener(topics = "registerUserTopic")
    public void registerUser(RegistrationEvent event) {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            usersRepoService.save(event.getUser());
        }
    }

    @KafkaListener(topics = "createDatabaseTopic")
    public void createDatabase(CreateDatabaseEvent event) {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            databaseService.createDatabase(event.getUsername(), event.getDatabaseName());
        }
    }

    @KafkaListener(topics = "deleteDatabaseTopic")
    public void deleteDatabase(DeleteDatabaseEvent event) {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            databaseService.deleteDatabase(event.getUsername(), event.getDatabaseName());
        }
    }

    @KafkaListener(topics = "addDocumentTopic")
    public void addDocument(AddDocumentEvent event) throws IOException,
            ExecutionException, InterruptedException,
            ProcessingException {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            collectionService.addDocument(event.getUsername(),
                    event.getDbName(),
                    event.getCollectionName(),
                    event.getObjectNode());
        }
    }

    @KafkaListener(topics = "deleteCollectionTopic")
    public void deleteCollection(DeleteCollectionEvent event) {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            collectionService.deleteCollection(event.getUsername(),
                    event.getDbName(),
                    event.getCollectionName());
        }
    }

    @KafkaListener(topics = "deleteDocumentTopic")
    public void deleteDocument(DeleteDocumentEvent event) throws IOException {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {

            documentService.deleteById(event.getDocumentId(), event.getUsername(),
                    event.getDbName(), event.getCollectionName());
        }

    }

    @KafkaListener(topics = "deleteAllDocumentsTopic")
    public <T> void deleteAllDocuments(DeleteAllDocumentsEvent<T> event) throws IOException {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            documentService.deleteMany(event.getCriteria(), event.getUsername(),
                    event.getDbName(), event.getCollectionName());
        }

    }

    @KafkaListener(topics = "newCollectionTopic")
    public void newCollection(NewCollectionEvent event) throws IOException {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            collectionService.writeCollection(event.getSchema(),
                    event.getUsername(),
                    event.getDbName(),
                    event.getCollection());
        }
    }

    @KafkaListener(topics = "newEmptyCollectionTopic")
    public void newEmptyCollection(NewEmptyCollectionEvent event) throws IOException {
        if (!event.getBroadcastingNodeName().equals(nodeName)) {
            collectionService.createNewEmptyCollection(event.getSchema(),
                    event.getUsername(),
                    event.getDbName(),
                    event.getCollectionName());
        }
    }
}
