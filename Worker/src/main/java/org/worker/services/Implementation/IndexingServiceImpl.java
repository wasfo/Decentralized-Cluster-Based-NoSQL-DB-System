package org.worker.services.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.stream.Collectors;


/**
 * index file format is 'fieldName'_index.json
 */

@Service
@Slf4j

public class IndexingServiceImpl implements IndexingService {
    private final HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;
    private static final Logger logger = LoggerFactory.getLogger(IndexingServiceImpl.class);
    private final CollectionService collectionService;
    private @Qualifier("storagePath") String storage_Path;

    @Autowired
    public IndexingServiceImpl(HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap,
                               CollectionService collectionService) {
        this.usersIndexesMap = usersIndexesMap;
        this.collectionService = collectionService;
    }

    @Override
    public ResponseEntity<String> createIndex(String username,
                                              String dbName,
                                              String collectionName,
                                              String fieldName) throws IOException {
        ObjectNode schema = collectionService.readSchema(username, dbName, collectionName);
        Path collectionDirectory = Path.of(storage_Path, username, dbName, collectionName);

        ResponseEntity<String> BAD_REQUEST = fieldNameExists(fieldName, schema);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        boolean indexAlreadyExists = indexAlreadyExists(collectionDirectory,
                new IndexObject(dbName, collectionName, fieldName, null));
        if (indexAlreadyExists) return DbUtils.getResponseEntity("index already exists",
                HttpStatus.CONFLICT);

        try {
            HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);
            if (indexingMap == null)
                indexingMap = new HashMap<>();

            Optional<Collection> collection = collectionService.
                    readCollection(username, dbName, collectionName);

            if (collection.isPresent()) {
                List<IndexObject> indexObjectList = new ArrayList<>();
                for (Document document : collection.get().getDocuments()) {
                    String value = document.getObjectNode().get(fieldName).asText();
                    IndexObject indexObject = new IndexObject(dbName, collectionName, fieldName, value);
                    indexObjectList.add(indexObject);
                    if (indexingMap.containsKey(indexObject)) {
                        indexingMap.get(indexObject).add(document.get_id());
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(document.get_id());
                        indexingMap.put(indexObject, list);
                    }
                }
                saveIndexes(indexObjectList, collectionDirectory);
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

    private void saveIndexes(List<IndexObject> indexObjects, Path collectionPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (IndexObject indexObject : indexObjects) {
            arrayNode.addPOJO(indexObject);
        }

        String indexesFileName = "indexes.json";
        Path indexFilePath = Path.of(collectionPath.toString(), indexesFileName);
        boolean isCreated = true;
        if (!indexFilePath.toFile().exists()) {
            isCreated = indexFilePath.toFile().createNewFile();
        }
        if (isCreated) {
            try (OutputStream outputStream = Files.newOutputStream(indexFilePath)) {
                objectMapper.writeValue(outputStream, arrayNode);
            }
        }
    }

    @Override
    public void removeIndex(String userDir,
                            String dbName,
                            String collectionName,
                            String fieldName) {

        removeIndexFromMap(userDir, dbName, collectionName, fieldName);
        removeIndexFromFile(userDir, dbName, collectionName, fieldName);

    }


    private void removeIndexFromFile(String userDir,
                                     String dbName,
                                     String collectionName,
                                     String fieldName) {
        Path indexesFilePath = Path.of(storage_Path,
                dbName,
                collectionName,
                "indexes.json");


    }

    private void removeIndexFromMap(String username,
                                    String dbName,
                                    String collectionName,
                                    String fieldName) {
        IndexObject indexObject = new IndexObject(dbName, collectionName, fieldName, null);
        HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);

        List<IndexObject> foundIndexes = indexingMap.keySet().stream().filter(
                entry ->
                        entry.getDbName().equals(indexObject.getDbName()) &&
                                entry.getCollectionName().equals(indexObject.getCollectionName()) &&
                                entry.getFieldName().equals(indexObject.getFieldName())
        ).toList();

        // foundIndexes.forEach(indexingMap::remove);

        for (IndexObject index : foundIndexes) {
            indexingMap.remove(index);
        }


    }

    @Override
    public List<Map.Entry<IndexObject, List<String>>> readIndex(String username,
                                                                String dbName,
                                                                String collectionName,
                                                                String fieldName) {
        IndexObject indexObject = new IndexObject(dbName, collectionName, fieldName, null);
        HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);
        List<Map.Entry<IndexObject, List<String>>> entries = indexingMap.entrySet().stream().filter(
                entry ->
                        entry.getKey().getDbName().equals(indexObject.getDbName()) &&
                                entry.getKey().getCollectionName().equals(indexObject.getCollectionName()) &&
                                entry.getKey().getFieldName().equals(indexObject.getFieldName())
        ).toList();

        return entries;
    }


    private static ResponseEntity<String> fieldNameExists(String fieldName, ObjectNode schema) {
        if (!schema.has(fieldName)) {
            return DbUtils.getResponseEntity("collection doesn't have" +
                            " the field name specified",
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    static private boolean indexAlreadyExists(Path pathOfCollectionDir, IndexObject targetObject) {
        Path path = Path.of(pathOfCollectionDir.toString(), "indexes.json");
        TypeReference<List<IndexObject>> typeReference = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        List<IndexObject> indexObjectList;
        try (InputStream inputStream = Files.newInputStream(path)) {
            indexObjectList = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return indexObjectList.stream().anyMatch(
                indexObject ->
                        indexObject.getDbName().equals(targetObject.getDbName()) &&
                                indexObject.getCollectionName().equals(targetObject.getCollectionName()) &&
                                indexObject.getFieldName().equals(targetObject.getFieldName()));
    }


    public static void main(String[] args) throws IOException {
        Path storagePath = Path.of(
                "Storage",
                "Node1" + "-Storage");
        Path path = Path.of(storagePath.toString(), "ahmad2@gmail.com", "db1", "students");
        IndexObject indexObject = new IndexObject("db1",
                "students",
                "age",
                null);

        System.out.println(indexAlreadyExists(path, indexObject));
    }
}