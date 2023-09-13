package org.worker.services.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.services.CollectionService;
import org.worker.utils.SchemaValidator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import java.util.concurrent.ExecutionException;

import static org.worker.utils.DbUtils.getResponseEntity;

@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    @Value("${node.name}")
    private String nodeName;
    private String storagePath;

    public CollectionServiceImpl(@Qualifier("storagePath") String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Optional<Collection> readCollection(String username,
                                               String dbName,
                                               String collectionName) throws IOException {

        Path collectionPath = Path.of(storagePath, username,
                dbName, collectionName, collectionName + ".json");

        if (!collectionPath.toFile().exists())
            return Optional.empty();
        TypeReference<Collection> typeReference = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = Files.newInputStream(collectionPath)) {
            Collection collection = mapper.readValue(inputStream, typeReference);
            return Optional.of(collection);
        }
    }


    @Override
    public ResponseEntity<String> deleteCollection(String userDir, String dbName, String collectionName) {
        Path path = Path.of(storagePath, userDir, dbName, collectionName);
        File file = path.toFile();
        if (file.exists()) {
            boolean fileDeleted = file.delete();
            if (fileDeleted)
                return getResponseEntity("Collection Deleted Successfully", HttpStatus.OK);
            return getResponseEntity("Collection does not exist", HttpStatus.OK);
        }
        return getResponseEntity("Something Went " +
                "Wrong deleting the collection: " + collectionName, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> writeCollection(ObjectNode schema, String userDir,
                                                  String dbName,
                                                  Collection collection) throws IOException {
        if (schema.isEmpty()) {
            return getResponseEntity("schema must be specified",
                    HttpStatus.BAD_REQUEST);
        }

        Path collectionDirectory = Path.of(storagePath, userDir, dbName, collection.getCollectionName());

        if (collectionDirectory.toFile().exists()) {
            return getResponseEntity("Collection with this Name already exists",
                    HttpStatus.NOT_ACCEPTABLE);
        } else {
            collectionDirectory.toFile().mkdir();
        }
        Path collectionPath = Path.of(storagePath, userDir, dbName, collection.getCollectionName(),
                collection.getCollectionName() + ".json");
        Path schemaPath = Path.of(storagePath, userDir, dbName,
                collection.getCollectionName(), "schema.json");

        boolean isCollectionCreated = collectionPath.toFile().createNewFile();
        boolean isSchemaCreated = schemaPath.toFile().createNewFile();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(collectionPath.toFile(), collection);
        objectMapper.writeValue(schemaPath.toFile(), schema);

        return getResponseEntity("collection created: " + isCollectionCreated
                        + "\n" + " schema created: " + isSchemaCreated
                , HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<String> createNewEmptyCollection(ObjectNode schema,
                                                           String userDir,
                                                           String dbName,
                                                           String collectionName) throws IOException {

        if (schema.isEmpty()) {
            return getResponseEntity("schema must be specified",
                    HttpStatus.BAD_REQUEST);
        }

        Path collectionDirectory = Path.of(storagePath, userDir, dbName, collectionName);

        if (collectionDirectory.toFile().exists()) {
            return getResponseEntity("Collection with this Name already exists",
                    HttpStatus.NOT_ACCEPTABLE);
        } else {
            collectionDirectory.toFile().mkdir();
        }

        Path collectionPath = Path.of(storagePath, userDir, dbName, collectionName,
                collectionName + ".json");
        Path schemaPath = Path.of(storagePath, userDir, dbName,
                collectionName, "schema.json");
        Path indexes = Path.of(storagePath, userDir, dbName,
                collectionName, "index.json");

        boolean isCollectionCreated = collectionPath.toFile().createNewFile();
        boolean isSchemaCreated = schemaPath.toFile().createNewFile();
        boolean isIndexesCreated = indexes.toFile().createNewFile();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(schemaPath.toFile(), schema);

        return getResponseEntity("collection created: " + isCollectionCreated
                        + "\n" + " schema created: " + isSchemaCreated
                        + "\n" + " indexes file: " + isIndexesCreated
                , HttpStatus.CREATED);

    }


    @Override
    public ResponseEntity<String> addDocument(String userDir, String dbName,
                                              String collectionName,
                                              ObjectNode objectNode) throws IOException, ProcessingException, ExecutionException, InterruptedException {
        // I should Create Data validation here.
        Path collectionPath = Path.of(storagePath, userDir, dbName, collectionName, collectionName + ".json");
        System.out.println("PATH -> " + collectionPath);
        if (!collectionPath.toFile().exists()) {
            return getResponseEntity("Collection with this Name does not exist",
                    HttpStatus.BAD_REQUEST);
        }
        ObjectNode schemaNode = readSchema(userDir, dbName, collectionName);
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
            Collection collection = readCollection(userDir, dbName, collectionName).get();
            collection.addDocument(document);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(collectionPath.toFile(), collection);
            return getResponseEntity("Document added Successfully",
                    HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<String> deleteDocument(String userDir,
                                                 String dbName,
                                                 String collectionName,
                                                 Document document) {

        return null;
    }

    @Override
    public ObjectNode readSchema(String userDir, String dbName, String collectionName) throws IOException {
        Path path = Path.of(storagePath, userDir, dbName, collectionName, "schema.json");
        try (InputStream inputStream = Files.newInputStream(path)) {
            return (ObjectNode) new ObjectMapper().readTree(inputStream);
        }
    }


}
