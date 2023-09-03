package org.worker.services.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.worker.deserializers.HashMapKeyDeserializer;
import org.worker.deserializers.HashMapKeySerializer;
import org.worker.deserializers.IndexObject;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.services.CollectionService;
import org.worker.services.IndexingService;
import org.worker.utils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.worker.constants.FilePaths.Storage_Path;

/**
 * index file format is 'fieldName'_index.json
 */

@Service
@Slf4j

public class IndexingServiceImpl implements IndexingService {
    private HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;
    private static final Logger logger = LoggerFactory.getLogger(IndexingServiceImpl.class);
    private CollectionService collectionService = new CollectionServiceImpl(new JsonServiceImpl());

    public IndexingServiceImpl() {
    }

    @Autowired
    public IndexingServiceImpl(HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap) {
        this.usersIndexesMap = usersIndexesMap;
    }

    @Override
    public ResponseEntity<String> createIndex(String username, String dbName, String collectionName,
                                              String fieldName) throws IOException {

        ObjectNode schema = collectionService.readSchema(username, dbName, collectionName);
        Path collectionDirectory = Path.of(Storage_Path, username, dbName, collectionName);

        ResponseEntity<String> BAD_REQUEST = fieldNameExists(fieldName, schema);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        ResponseEntity<String> BAD_REQUEST1 = isFieldAlreadyIndexed(fieldName, collectionDirectory);
        if (BAD_REQUEST1 != null) return BAD_REQUEST1;

        try {
            Optional<Collection> collection = collectionService.readCollection(username, dbName, collectionName);
            if (collection.isPresent()) {
                HashMap<IndexObject, List<String>> indexMap = usersIndexesMap.get(username);
                for (Document document : collection.get().getDocuments()) {
                    String value = document.getObjectNode().get(fieldName).asText();
                    IndexObject indexObject = new IndexObject(dbName, collectionName, fieldName, value);
                    if (indexMap.containsKey(indexObject))
                        indexMap.get(indexObject).add(document.get_id());
                    else {
                        List<String> list = new ArrayList<>();
                        list.add(document.get_id());
                        indexMap.put(indexObject, list);
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                String indexFileName = fieldName + "_" + "index.json";
                Path indexFilePath = Path.of(collectionDirectory.toString(), indexFileName);
                System.out.println(indexFilePath);
                boolean isCreated = indexFilePath.toFile().createNewFile();
                if (isCreated) {
                    try (OutputStream outputStream = Files.newOutputStream(indexFilePath)) {
                        objectMapper.writeValue(outputStream, indexMap);
                    }
                }
            }
            System.out.println(usersIndexesMap);

        } catch (IOException exception) {
            logger.error("An error occurred in indexing:", exception);
            return DbUtils.getResponseEntity("internal server error in indexing service",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return DbUtils.getResponseEntity("indexing on field: " + fieldName + " created successfully",
                HttpStatus.OK);
    }

    private static ResponseEntity<String> isFieldAlreadyIndexed(String fieldName, Path collectionDirectory) {
        if (indexAlreadyExists(collectionDirectory, fieldName)) {
            return DbUtils.getResponseEntity("this field is already indexed",
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    private static ResponseEntity<String> fieldNameExists(String fieldName, ObjectNode schema) {
        if (!schema.has(fieldName)) {
            return DbUtils.getResponseEntity("collection doesn't have the field name specified",
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    static private boolean indexAlreadyExists(Path pathOfCollectionDir, String fieldName) {
        if (!pathOfCollectionDir.toFile().exists())
            return false;

        String indexFile = fieldName + "_index.json";
        Optional<String> optionalOfFile = Arrays.stream(Objects.requireNonNull(pathOfCollectionDir.toFile().listFiles()))
                .map(File::getName)
                .filter(fileName -> fileName.equals(indexFile))
                .findFirst();

        return optionalOfFile.isPresent();
    }

    @Override
    public void removeIndex(String username, String dbName, String collectionName, String fieldName) {
        IndexObject indexObject = new IndexObject();
        indexObject.setCollectionName(collectionName);
        indexObject.setFieldName(fieldName);
        usersIndexesMap.get(username).remove(indexObject);

    }

    @Override
    public HashMap<IndexObject, List<String>> readIndex(String userDir, String dbName, String collectionName, String fieldName) throws IOException {
        Path indexPath = Path.of(userDir, dbName, collectionName, fieldName + "_index.json");
        TypeReference<HashMap<IndexObject, List<String>>> typeReference = new TypeReference<>() {
        };
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(HashMap.class, new HashMapKeySerializer());
        module.addKeyDeserializer(HashMap.class, new HashMapKeyDeserializer());
        objectMapper.registerModule(module);
        try (InputStream inputStream = Files.newInputStream(indexPath)) {
            HashMap<IndexObject, List<String>> indexMap = objectMapper.readValue(inputStream, typeReference);
            return indexMap;
        }
    }

    public HashMap<String, HashMap<IndexObject, List<String>>> getUsersIndexesMap() {
        return usersIndexesMap;
    }

}
