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
import org.worker.locks.FileLockObject;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.services.CollectionService;
import org.worker.utils.SchemaValidator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
    public ResponseEntity<String> deleteCollection(String userDir, String dbName, String collectionName) throws IOException {
        Path path = Path.of(storagePath, userDir, dbName, collectionName);
        File file = path.toFile();
        FileLockObject lock = new FileLockObject(file);
        lock.createLock();
        if (file.exists()) {
            boolean fileDeleted = file.delete();
            lock.releaseLock();
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

        FileLockObject lock = new FileLockObject(collectionDirectory.toFile());
        lock.createLock();

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

        lock.releaseLock();

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
        File colFile = collectionDirectory.toFile();


        if (collectionDirectory.toFile().exists()) {
            return getResponseEntity("Collection with this Name already exists",
                    HttpStatus.NOT_ACCEPTABLE);
        } else {
            colFile.mkdir();
        }

        FileLockObject lock = new FileLockObject(colFile);
        lock.createLock();
        Path collectionPath = Path.of(storagePath, userDir, dbName, collectionName,
                collectionName + ".json");
        Path schemaPath = Path.of(storagePath, userDir, dbName,
                collectionName, "schema.json");

        boolean isCollectionCreated = collectionPath.toFile().createNewFile();
        boolean isSchemaCreated = schemaPath.toFile().createNewFile();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(schemaPath.toFile(), schema);

        lock.releaseLock();
        return getResponseEntity("collection created: " + isCollectionCreated
                        + "\n" + " schema created: " + isSchemaCreated
                        + "\n" + " indexes file: "
                , HttpStatus.CREATED);

    }


    @Override
    public ObjectNode readSchema(String userDir, String dbName, String collectionName) throws IOException {
        Path path = Path.of(storagePath, userDir, dbName, collectionName, "schema.json");
        File schemaFile = path.toFile();
        FileLockObject lock = new FileLockObject(schemaFile);
        lock.createLock();

        try (InputStream inputStream = Files.newInputStream(path)) {
            ObjectNode node = (ObjectNode) new ObjectMapper().readTree(inputStream);
            inputStream.close();
            lock.releaseLock();
            return node;
        }

    }


}
