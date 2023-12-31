package org.worker.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface CollectionService {
    public Optional<Collection> readCollection(String username, String dbName, String collectionName) throws IOException;

    public ResponseEntity<String> deleteCollection(String userDir, String dbName, String collectionName) throws IOException;

    public ResponseEntity<String> writeCollection(ObjectNode schema, String userDir, String DbName, Collection collection) throws IOException;

    public ResponseEntity<String> createNewEmptyCollection(ObjectNode schema, String username,
                                                           String dbName, String collectionName) throws IOException;
    public ObjectNode readSchema(String userDir, String dbName, String collectionName) throws IOException;
}
