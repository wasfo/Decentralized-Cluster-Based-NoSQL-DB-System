package org.worker.services.Implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Qualifier;
import org.worker.deserializers.IndexObject;
import org.worker.locks.FileLockObject;
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
import org.worker.utils.DbUtils;
import org.worker.utils.SchemaValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;


import static org.worker.utils.DbUtils.getResponseEntity;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JsonService jsonService;
    private final IndexingService indexingService;
    private final CollectionService collectionService;
    private final String storagePath;
    private final HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;


    @Autowired
    public DocumentServiceImpl(@Qualifier("storagePath") String storagePath,
                               JsonService jsonService,
                               CollectionService collectionService,
                               IndexingService indexingService, HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap) {
        this.storagePath = storagePath;
        this.jsonService = jsonService;
        this.collectionService = collectionService;
        this.indexingService = indexingService;
        this.usersIndexesMap = usersIndexesMap;
    }


    @Override
    public ResponseEntity<?> readDocumentById(String username,
                                              String dbName,
                                              String collectionName,
                                              String id) throws IOException {


        Path colPath = Path.of(storagePath, username,
                dbName,
                collectionName,
                collectionName + ".json");


        File docFile = colPath.toFile();
        FileLockObject lock = new FileLockObject(docFile);
        lock.createLock();


        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(colPath.toFile()).get("documents");


        lock.releaseLock();

        for (JsonNode node : arrayNode) {
            if (node.get("_id").asText().equals(id))
                return new ResponseEntity<>(node, HttpStatus.OK);
        }


        return new ResponseEntity<>("Sorry doc with such id does not exit", HttpStatus.NOT_FOUND);

    }


    @Override
    public ResponseEntity<List<JsonNode>> readDocumentsByCriteria(String username,
                                                                  String dbName,
                                                                  String collectionName,
                                                                  JsonProperty<?> jsonProperty) throws IOException {


        Path colPath = Path.of(storagePath, username, dbName, collectionName, collectionName + ".json");


        IndexObject indexObject = new IndexObject(dbName, collectionName, jsonProperty.getKey(), jsonProperty.getValue());
        Optional<List<String>> documentIds = indexingService.readSpecificIndex(username, indexObject);
        if (documentIds.isPresent()) {
            List<JsonNode> docs = readDocuments(colPath, documentIds.get());
            return ResponseEntity.ok(docs);
        }
        List<JsonNode> documents = readDocumentsByCriteria(colPath, jsonProperty);
        return ResponseEntity.ok(documents);
    }

    public List<JsonNode> readDocuments(Path collectionPath, List<String> documentIds) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        FileLockObject lock = new FileLockObject(collectionPath.toFile());
        lock.createLock();
        ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(collectionPath.toFile()).get("documents");
        lock.releaseLock();

        List<JsonNode> documentsList = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            String docId = node.get("_id").asText();
            if (documentIds.contains(docId))
                documentsList.add(node);
        }

        return documentsList;
    }

    public List<JsonNode> readDocumentsByCriteria(Path collectionPath,
                                                  JsonProperty<?> jsonProperty) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        FileLockObject lock = new FileLockObject(collectionPath.toFile());
        lock.createLock();

        ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(collectionPath.toFile()).get("documents");
        lock.releaseLock();

        List<JsonNode> documentsList = new ArrayList<>();
        String keyToFind = jsonProperty.getKey();
        Object valueToFind = jsonProperty.getValue();
        for (JsonNode node : arrayNode) {
            if (node.has(keyToFind)) {
                Object value = node.get(keyToFind);
                if (value.equals(valueToFind))
                    documentsList.add(node);
            }
        }
        return documentsList;
    }


    @Override
    public ResponseEntity<String> deleteById(String targetId,
                                             String userDir,
                                             String dbName,
                                             String collectionName) throws IOException {
        String path = String.valueOf(Path.of(storagePath, userDir, dbName,
                collectionName, collectionName + ".json"));

        FileLockObject lock = new FileLockObject(new File(path));
        lock.createLock();

        boolean isDeleted = jsonService.deleteFirst(path, new JsonProperty<>("_id", targetId));
        lock.releaseLock();

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
        File collectionFile = collectionPath.toFile();

        FileLockObject lock = new FileLockObject(collectionPath.toFile());
        lock.createLock();

        if (!collectionPath.toFile().exists()) {
            return getResponseEntity("Collection with this Name does not exist",
                    HttpStatus.BAD_REQUEST);
        }

        ObjectNode schemaNode = collectionService.readSchema(userDir, dbName, collectionName);
        Document document = Document.createEmptyDocument();
        document.setObjectNode(objectNode);

        // replace with schema validator
        boolean isValidDocument = SchemaValidator.isValidDocument(schemaNode, document.getObjectNode());

        if (!isValidDocument)
            return getResponseEntity("Document fields does not follow the schema structure",
                    HttpStatus.BAD_REQUEST);
        else {
            Collection collection = collectionService.readCollection(userDir, dbName, collectionName).get();
            collection.addDocument(document);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(collectionFile, collection);

            lock.releaseLock();

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

        FileLockObject lock = new FileLockObject(new File(path));
        lock.createLock();

        jsonService.deleteAll(path, criteria);

        lock.releaseLock();
        return getResponseEntity("document deleted successfully", HttpStatus.OK);
    }


}
