package org.worker.services.Implementation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.services.CollectionService;
import org.worker.services.JsonService;
import org.worker.utils.SchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import static org.worker.constants.FilePaths.Storage_Path;
import static org.worker.utils.DbUtils.getResponseEntity;

@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    private final JsonService jsonService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CollectionServiceImpl(JsonService jsonService) {
        this.jsonService = jsonService;
    }


    @Override
    public Optional<Collection> readCollection(Path collectionDirectory) throws IOException {
        if (!collectionDirectory.toFile().exists())
            return Optional.empty();

        Path path = Path.of(collectionDirectory.toString(), collectionDirectory.getFileName() + ".json");
        TypeReference<Collection> typeReference = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = Files.newInputStream(path)) {
            Collection collection = mapper.readValue(inputStream, typeReference);
            return Optional.of(collection);
        }
    }

    public String extractCollectionName(String collectionName) {
        File collectionfile = new File(collectionName);
        int extensionIndex = collectionfile.getName().indexOf(".json");
        return collectionfile.getName().substring(0, extensionIndex);
    }

    @Override
    public ResponseEntity<String> deleteCollection(String userDir, String dbName, String collectionName) {
        Path path = Path.of(Storage_Path, userDir, dbName, collectionName);
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

        Path collectionDirectory = Path.of(Storage_Path, userDir, dbName, collection.getCollectionName());

        if (collectionDirectory.toFile().exists()) {
            return getResponseEntity("Collection with this Name already exists",
                    HttpStatus.BAD_REQUEST);
        } else {
            collectionDirectory.toFile().mkdir();
        }
        Path collectionPath = Path.of(Storage_Path, userDir, dbName, collection.getCollectionName(),
                collection.getCollectionName() + ".json");
        Path schemaPath = Path.of(Storage_Path, userDir, dbName,
                collection.getCollectionName(), "schema.json");

        boolean isCollectionCreated = collectionPath.toFile().createNewFile();
        boolean isSchemaCreated = schemaPath.toFile().createNewFile();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(collectionPath.toFile(), collection);
        objectMapper.writeValue(schemaPath.toFile(), schema);

        return getResponseEntity("collection created: " + isCollectionCreated
                        + "\n" + " schema created: " + isSchemaCreated
                , HttpStatus.OK);

    }


    @Override
    public ResponseEntity<String> addDocument(String userDir, String dbName,
                                              String collectionName, Document document) throws IOException, ProcessingException {
        // I should Create Data validation here.
        Path path = Path.of(Storage_Path, userDir, dbName, collectionName + ".json");
        if (path.toFile().exists()) {
            return getResponseEntity("Collection with this Name already exists",
                    HttpStatus.BAD_REQUEST);
        }
        ObjectNode schemaNode = readSchema(userDir, dbName, collectionName);

        // replace with schema validator
        boolean isValidDocument = SchemaValidator.isValidDocument(schemaNode, document);

        if (!isValidDocument)
            return getResponseEntity("Document fields does not follow the schema structure",
                    HttpStatus.BAD_REQUEST);
        else {
            jsonService.addJsonNodeToJsonArray(new File(path.toString()), document.getObjectNode());
            return getResponseEntity("Document added Successfully",
                    HttpStatus.OK);
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
        Path path = Path.of(Storage_Path, userDir, dbName, collectionName, "schema.json");
        try (InputStream inputStream = Files.newInputStream(path)) {
            return (ObjectNode) new ObjectMapper().readTree(inputStream);
        }
    }



    public static void createTestCollection() {
        Document student1 = Document.createEmptyDocument();
        student1.put("name", "Ahmad");
        student1.put("age", 15);

        Document student2 = Document.createEmptyDocument();
        student2.put("name", "Ali");
        student2.put("age", 23);

        Collection collection = new Collection("students");
        collection.addDocument(student1);
        collection.addDocument(student2);

        ObjectNode schema = new ObjectMapper().createObjectNode();
        schema.put("name", "String");
        schema.put("age", "Integer");
    }


}
