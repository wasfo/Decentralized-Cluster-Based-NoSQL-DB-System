package org.worker.services;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.worker.models.Document;
import org.worker.models.JsonProperty;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface DocumentService {

    public ResponseEntity<?> readDocumentById(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String id) throws IOException, ProcessingException, ExecutionException, InterruptedException;

    public ResponseEntity<List<Document>> readDocumentsByCriteria(String userDir,
                                                                  String dbName,
                                                                  String collectionName,
                                                                  JsonProperty<?> jsonProperty) throws IOException, ProcessingException;

    public ResponseEntity<String> deleteById(String id,
                                             String userDir,
                                             String dbName,
                                             String collectionName) throws IOException;

    public <T> ResponseEntity<String> deleteMany(JsonProperty<T> criteria,
                                                 String userDir,
                                                 String dbName,
                                                 String collectionName) throws IOException;

    ResponseEntity<String> addDocumentToCollection(String userDir, String dbName,
                                                   String collectionName,
                                                   ObjectNode objectNode) throws IOException, ProcessingException;

}
