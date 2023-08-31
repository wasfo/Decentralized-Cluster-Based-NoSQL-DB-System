package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.worker.constants.FilePaths.Storage_Path;
import static org.worker.utils.DbUtils.getResponseEntity;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JsonService jsonService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CollectionService collectionService;


    @Autowired
    public DocumentServiceImpl(JsonService jsonService, CollectionService collectionService) {
        this.jsonService = jsonService;
        this.collectionService = collectionService;
    }


    @Override
    public ResponseEntity<?> readDocumentById(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String id) throws IOException {

        Path path = Path.of(Storage_Path, userDir, dbName, collectionName, collectionName + ".json");
        if (!path.toFile().exists()) {
            return new ResponseEntity<>("collection doesn't not exist", HttpStatus.BAD_REQUEST);
        }
        Collection collection = collectionService.readCollection(path).get();
        for (Document document : collection.getDocuments()) {
            if (document.get_id().equals(id)) {
                return new ResponseEntity<>(document, HttpStatus.OK);
            }
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
