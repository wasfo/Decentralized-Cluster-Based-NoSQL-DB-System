package org.worker.services;


import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.worker.models.Document;
import org.worker.models.JsonProperty;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DocumentService {

    public ResponseEntity<String> addDocument(String userDir,
                                                 String dbName,
                                                 String collectionName,
                                                 Document document) throws IOException, ProcessingException;


    public ResponseEntity<String> deleteById(String id,
                                                 String userDir,
                                                 String dbName,
                                                 String collectionName) throws IOException;

    public <T> ResponseEntity<String> deleteMany(JsonProperty<T> criteria,
                                                 String userDir,
                                                 String dbName,
                                                 String collectionName) throws IOException;

}
