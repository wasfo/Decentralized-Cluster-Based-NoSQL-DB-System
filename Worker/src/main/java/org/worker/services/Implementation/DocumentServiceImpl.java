package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.worker.models.Collection;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


import static org.worker.utils.DbUtils.getResponseEntity;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JsonService jsonService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CollectionService collectionService;
    private String storagePath;


    @Autowired
    public DocumentServiceImpl(@Qualifier("storagePath") String storagePath,
                               JsonService jsonService, CollectionService collectionService) {
        this.storagePath = storagePath;
        this.jsonService = jsonService;
        this.collectionService = collectionService;
    }


    @Override
    public ResponseEntity<?> readDocumentById(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String id) throws IOException, ExecutionException, InterruptedException {
        Optional<Collection> collection = collectionService.readCollection(userDir, dbName, collectionName);
        if (collection.isPresent()) {
            for (Document document : collection.get().getDocuments()) {
                if (document.get_id().equals(id)) {
                    return new ResponseEntity<>(document, HttpStatus.OK);
                }
            }
        } else {
            new ResponseEntity<>("Collection "
                    + collectionName + "is Empty", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>("document was not found in "
                + collectionName + " collection", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<Document>> readDocumentsByCriteria(String userDir,
                                                                  String dbName,
                                                                  String collectionName,
                                                                  JsonProperty<?> jsonProperty) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteById(String targetId,
                                             String userDir,
                                             String dbName,
                                             String collectionName) throws IOException {
        String path = String.valueOf(Path.of(storagePath, userDir, dbName,
                collectionName, collectionName + ".json"));

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
