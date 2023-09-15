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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


/**
 * index file format is 'fieldName'_index.json
 */

@Service
@Slf4j

public class IndexingServiceImpl implements IndexingService {
    private final HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap;
    private static final Logger logger = LoggerFactory.getLogger(IndexingServiceImpl.class);
    private final CollectionService collectionService;
    private final String storagePath;

    @Autowired
    public IndexingServiceImpl(@Qualifier("storagePath") String storage_Path,
                               HashMap<String, HashMap<IndexObject, List<String>>> usersIndexesMap,
                               CollectionService collectionService) {
        this.storagePath = storage_Path;
        this.usersIndexesMap = usersIndexesMap;
        this.collectionService = collectionService;
    }

    @Override
    public ResponseEntity<String> createIndex(String username,
                                              String dbName,
                                              String collectionName,
                                              String fieldName) throws IOException {

        ObjectNode schema = collectionService.readSchema(username, dbName, collectionName);
        Path indexesPath = Path.of(storagePath, username);

        ResponseEntity<String> response = fieldNameExists(fieldName, schema);
        if (response != null)
            return response;

        boolean indexAlreadyExists = indexAlreadyExists(indexesPath,
                new IndexObject(dbName, collectionName, fieldName, null));
        if (indexAlreadyExists)
            return DbUtils.getResponseEntity("index already exists",
                    HttpStatus.CONFLICT);

        IndexObject indexObjectToBeSaved = new IndexObject(dbName, collectionName, fieldName, null);

        boolean areLoaded = loadIndexToMap(username, dbName, collectionName, fieldName);
        if (!areLoaded)
            return DbUtils.getResponseEntity("internal server error in indexing service",
                    HttpStatus.INTERNAL_SERVER_ERROR);

        saveIndexes(List.of(indexObjectToBeSaved), indexesPath);
        return DbUtils.getResponseEntity("indexing on field: " +
                        fieldName + " created successfully",
                HttpStatus.CREATED);
    }

    @Override
    public boolean loadIndexToMap(String username,
                                  String dbName,
                                  String collectionName,
                                  String fieldName) {
        try {
            HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);
            if (indexingMap == null)
                indexingMap = new HashMap<>();

            Optional<Collection> collection = collectionService.
                    readCollection(username, dbName, collectionName);
            if (collection.isPresent()) {
                for (Document document : collection.get().getDocuments()) {
                    String value = document.getObjectNode().get(fieldName).asText();
                    IndexObject indexObject = new IndexObject(dbName, collectionName, fieldName, value);
                    if (indexingMap.containsKey(indexObject)) {
                        indexingMap.get(indexObject).add(document.get_id());
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(document.get_id());
                        indexingMap.put(indexObject, list);
                    }
                }
            }
        } catch (IOException exception) {
            logger.error("An error occurred in indexing:", exception);
            return false;
        }
        return true;
    }

    @Override
    public void loadAllIndexesToMap(String username) {
        List<IndexObject> indexObjects = readIndexObjects(Path.of(storagePath, username));
        if (indexObjects.isEmpty())
            return;

        for (IndexObject object : indexObjects) {
            loadIndexToMap(username, object.getDbName(), object.getCollectionName(), object.getFieldName());
        }
    }

    private void saveIndexes(List<IndexObject> indexObjects, Path indexesPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (IndexObject indexObject : indexObjects) {
            arrayNode.addPOJO(indexObject);
        }

        String indexesFileName = "indexes.json";
        Path indexFilePath = Path.of(indexesPath.toString(), indexesFileName);
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
                            String fieldName) throws IOException {

        removeIndexFromMap(userDir, dbName, collectionName, fieldName);
        removeIndexFromFile(userDir, dbName, collectionName, fieldName);

    }


    private void removeIndexFromFile(String username,
                                     String dbName,
                                     String collectionName,
                                     String fieldName) throws IOException {
        Path indexesPath = Path.of(storagePath, username);
        IndexObject targetObject = new IndexObject(dbName, collectionName, fieldName, null);
        List<IndexObject> indexObjectList = readIndexObjects(indexesPath);
        indexObjectList.removeIf(object -> object.equals(targetObject));

        saveIndexes(indexObjectList, indexesPath);
    }

    private void removeIndexFromMap(String username,
                                    String dbName,
                                    String collectionName,
                                    String fieldName) {
        IndexObject targetObject = new IndexObject(dbName, collectionName, fieldName, null);
        HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);

        List<IndexObject> foundIndexes = indexingMap
                .keySet()
                .stream()
                .filter(entry -> entry.equals(targetObject)).toList();

        // foundIndexes.forEach(indexingMap::remove);

        for (IndexObject index : foundIndexes) {
            indexingMap.remove(index);
        }

    }

    @Override
    public Optional<List<String>> readSpecificIndex(String username,
                                                    IndexObject targetObject) {

        HashMap<IndexObject, List<String>> indexingMap = usersIndexesMap.get(username);

        Stream<Map.Entry<IndexObject, List<String>>> entriesStream = indexingMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getDbName().equals(targetObject.getDbName()) &&
                        entry.getKey().getCollectionName().equals(targetObject.getCollectionName()) &&
                        entry.getKey().getFieldName().equals(targetObject.getFieldName()) &&
                        entry.getKey().getValue().equals(targetObject.getValue()));

        Optional<Map.Entry<IndexObject, List<String>>> targetEntry = entriesStream.findFirst();
        return targetEntry.map(Map.Entry::getValue);

    }


    private static ResponseEntity<String> fieldNameExists(String fieldName, ObjectNode schema) {
        if (!schema.has(fieldName)) {
            return DbUtils.getResponseEntity("collection doesn't have" +
                            " the field name specified",
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    static private boolean indexAlreadyExists(Path indexesPath, IndexObject targetObject) {
        List<IndexObject> indexObjectList = readIndexObjects(indexesPath);
        return indexObjectList.stream().anyMatch(
                indexObject -> indexObject.equals(targetObject));
    }

    private static List<IndexObject> readIndexObjects(Path indexesPath) {
        String indexesFileName = "indexes.json";
        Path indexFilePath = Path.of(indexesPath.toString(), indexesFileName);
        if (indexFilePath.toFile().length() == 0)
            return Collections.emptyList();
        TypeReference<List<IndexObject>> typeReference = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        List<IndexObject> indexObjectList;
        try (InputStream inputStream = Files.newInputStream(indexFilePath)) {
            indexObjectList = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return indexObjectList;
    }

}