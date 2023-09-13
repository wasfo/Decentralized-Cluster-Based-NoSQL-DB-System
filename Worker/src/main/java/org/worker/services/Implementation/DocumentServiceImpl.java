package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.models.JsonProperty;
import org.worker.services.CollectionService;
import org.worker.services.DocumentService;
import org.worker.services.IndexingService;
import org.worker.services.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.worker.utils.SchemaValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


import static org.worker.utils.DbUtils.getResponseEntity;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JsonService jsonService;
    private final IndexingService indexingService;
    private final CollectionService collectionService;
    private final String storagePath;


    @Autowired
    public DocumentServiceImpl(@Qualifier("storagePath") String storagePath,
                               JsonService jsonService,
                               CollectionService collectionService,
                               IndexingService indexingService) {
        this.storagePath = storagePath;
        this.jsonService = jsonService;
        this.collectionService = collectionService;
        this.indexingService = indexingService;
    }


    @Override
    public ResponseEntity<?> readDocumentById(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String id) throws IOException {

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
    public ResponseEntity<String> addDocumentToCollection(String userDir, String dbName,
                                              String collectionName,
                                              ObjectNode objectNode) throws IOException, ProcessingException {
        // I should Create Data validation here.
        Path collectionPath = Path.of(storagePath, userDir, dbName, collectionName, collectionName + ".json");
        System.out.println("PATH -> " + collectionPath);
        if (!collectionPath.toFile().exists()) {
            return getResponseEntity("Collection with this Name does not exist",
                    HttpStatus.BAD_REQUEST);
        }
        ObjectNode schemaNode = collectionService.readSchema(userDir, dbName, collectionName);
        Document document = Document.createEmptyDocument();
        document.setObjectNode(objectNode);
        System.out.println("document ->" + document);
        // replace with schema validator
        boolean isValidDocument = SchemaValidator.isValidDocument(schemaNode, document);
        System.out.println("is valid document -> " + isValidDocument);
        if (!isValidDocument)
            return getResponseEntity("Document fields does not follow the schema structure",
                    HttpStatus.BAD_REQUEST);
        else {
            Collection collection = collectionService.readCollection(userDir, dbName, collectionName).get();
            collection.addDocument(document);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(collectionPath.toFile(), collection);
            return getResponseEntity("Document added Successfully",
                    HttpStatus.CREATED);
        }
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
