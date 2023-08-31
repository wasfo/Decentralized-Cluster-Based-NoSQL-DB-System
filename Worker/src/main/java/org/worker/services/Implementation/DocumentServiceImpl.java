package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.worker.models.Document;
import org.worker.models.JsonProperty;
import org.worker.services.CollectionService;
import org.worker.services.DocumentService;
import org.worker.services.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

import static org.worker.utils.DbUtils.getResponseEntity;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JsonService jsonService;
    private final CollectionService collectionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DocumentServiceImpl(JsonService jsonService, CollectionService collectionService) {
        this.jsonService = jsonService;
        this.collectionService = collectionService;
    }

    @Override
    public ResponseEntity<String> addDocument(String userDir, String dbName, String collectionName, Document document) throws IOException, ProcessingException {
        return collectionService.addDocument(userDir, dbName, collectionName, document);
    }

    @Override
    public ResponseEntity<String> deleteById(String targetId,
                                             String userDir,
                                             String dbName,
                                             String collectionName) throws IOException {
        String path = String.valueOf(Path.of(userDir, dbName, collectionName));
        boolean isDeleted = jsonService.deleteFirst(path, new JsonProperty<>("_id", targetId));
        if (isDeleted) {
            return getResponseEntity("document deleted successfully", HttpStatus.OK);
        }
        return getResponseEntity("something went wrong in deleting this document with id: "
                + targetId, HttpStatus.BAD_REQUEST);
    }

    @Override
    public <T> ResponseEntity<String> deleteMany(JsonProperty<T> criteria,
                                                 String userDir,
                                                 String dbName,
                                                 String collectionName) throws IOException {

        String path = String.valueOf(Path.of(userDir, dbName, collectionName));
        jsonService.deleteAll(path, criteria);
        return getResponseEntity("document deleted successfully", HttpStatus.OK);
    }


}
