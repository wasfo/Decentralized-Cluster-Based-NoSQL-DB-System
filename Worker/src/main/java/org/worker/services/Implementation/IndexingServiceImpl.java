package org.worker.services.Implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.worker.datastructure.FieldValueMap;
import org.worker.datastructure.IndexingMap;
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
import java.util.concurrent.ExecutionException;

import static org.worker.constants.FilePaths.Storage_Path;

/**
 * index file format is 'fieldName'_index.json
 */

@Service
@Slf4j

public class IndexingServiceImpl implements IndexingService {
    private HashMap<String, IndexingMap> usersIndexesMap;
    private static final Logger logger = LoggerFactory.getLogger(IndexingServiceImpl.class);
    private CollectionService collectionService = new CollectionServiceImpl(new JsonServiceImpl());

    public IndexingServiceImpl() {
    }


    @Autowired
    public IndexingServiceImpl(HashMap<String, IndexingMap> usersIndexesMap) {
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
            Optional<Collection> collection = collectionService.readCollection(username, dbName, collectionName).get();
            if (collection.isPresent()) {
                IndexingMap indexingMap = usersIndexesMap.get(username);

                indexingMap.putDatabase(dbName);
                indexingMap.putCollection(dbName, collectionName);
                System.out.println("indexing map ->" + indexingMap.getDatabasesMap(dbName));
                HashMap<Map.Entry<String, Object>, List<String>> map =
                        indexingMap.getFieldValueMap(collectionName).getMap();

                for (Document document : collection.get().getDocuments()) {
                    String value = document.getObjectNode().get(fieldName).asText();
                    Map.Entry<String, Object> entry = Map.entry(fieldName, value);
                    if (map.containsKey(entry))
                        map.get(entry).add(document.get_id());
                    else {
                        List<String> list = new ArrayList<>();
                        list.add(document.get_id());
                        map.put(entry, list);
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                String indexFileName = fieldName + "_" + "index.json";
                Path indexFilePath = Path.of(collectionDirectory.toString(), indexFileName);
                boolean isCreated = indexFilePath.toFile().createNewFile();
                if (isCreated) {
                    try (OutputStream outputStream = Files.newOutputStream(indexFilePath)) {
                        objectMapper.writeValue(outputStream, map);
                    }
                }
            }
            System.out.println(usersIndexesMap);

        } catch (IOException exception) {
            logger.error("An error occurred in indexing:", exception);
            return DbUtils.getResponseEntity("internal server error in indexing service",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
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
        usersIndexesMap.get(username).getFieldValueMap(collectionName);
        System.out.println("inside removed index method -> " + usersIndexesMap);

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

    public HashMap<String, IndexingMap> getUsersIndexesMap() {
        return usersIndexesMap;
    }


    public static void main(String[] args) {
        IndexObject indexObject1 = new IndexObject("students", "age", "32");
        IndexObject indexObject2 = new IndexObject("students", "age", "55");
        HashMap<IndexObject, Integer> map = new HashMap<>();
        map.put(indexObject1, 3);
        map.put(indexObject2, 2);
        System.out.println(map);
    }
}
