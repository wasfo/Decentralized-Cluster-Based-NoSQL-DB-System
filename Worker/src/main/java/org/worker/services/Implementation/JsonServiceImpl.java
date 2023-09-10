package org.worker.services.Implementation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.worker.models.Collection;
import org.worker.models.Document;
import org.worker.models.JsonProperty;
import org.worker.services.JsonService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class JsonServiceImpl implements JsonService {
    @Override
    public <T> List<T> loadFromJson(String path, Class<T> clazz) {
        List<T> collection = new ArrayList<>();
        try (JsonParser jsonParser = new JsonFactory().
                createParser(Files.newBufferedReader(Paths.get(path)))) {
            ObjectMapper objectMapper = new ObjectMapper();
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                T t = objectMapper.readValue(jsonParser, clazz);
                collection.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return collection;
    }

    @Override
    public Optional<JsonNode> readArray(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        JsonNode rootNode = null;
        if (jsonFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            rootNode = objectMapper.readTree(jsonFile);

        }
        assert rootNode != null;
        return Optional.of(rootNode);
    }

    @Override
    public Optional<Collection> readArrayIntoCollection(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        int extensionIndex = jsonFile.getName().indexOf(".json");
        String colName = jsonFile.getName().substring(0, extensionIndex);
        Collection collection = new Collection(colName);

        Optional<JsonNode> rootNode = readArray(filePath);
        List<Document> documents = new ArrayList<>();
        if (rootNode.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            for (JsonNode jsonNode : rootNode.get()) {
                Document document = mapper.convertValue(jsonNode, Document.class);
                documents.add(document);
            }
            collection.setDocuments(documents);
            return Optional.of(collection);
        }

        return Optional.empty();
    }

    @Override
    public void writeArray(String filePath, ArrayNode node) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), node);
    }

    @Override
    public void addJsonNodeToJsonArray(File jsonFile, ObjectNode newObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonFile);

        if (rootNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) rootNode;
            arrayNode.add(newObject);
            objectMapper.writeValue(jsonFile, arrayNode);
        }

    }

    @Override
    public boolean deleteFirst(String filePath, JsonProperty<?> jsonProperty) throws IOException {
        File jsonFile = new File(filePath);

        if (jsonFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            String keyToDelete = jsonProperty.getKey();
            Object valueToDelete = jsonProperty.getValue();
            ArrayNode arrayNode = (ArrayNode) rootNode.get("documents");
            if (arrayNode.isEmpty())
                return false;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode jsonNode = arrayNode.get(i);
                if (jsonNode.has(keyToDelete)) {
                    String foundValue = jsonNode.get(keyToDelete).asText();
                    if (foundValue.equals(valueToDelete)) {
                        ((ArrayNode) rootNode.get("documents")).remove(i);
                        objectMapper.writeValue(jsonFile, rootNode);
                        return true;
                    }

                }
            }

        }
        return false;
    }

    @Override
    public void deleteAll(String filePath, JsonProperty<?> jsonProperty) throws IOException {
        File jsonFile = new File(filePath);

        if (jsonFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            String keyToDelete = jsonProperty.getKey();
            Object valueToDelete = jsonProperty.getValue();

            if (rootNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) rootNode;
                for (int i = 0; i < arrayNode.size(); i++) {
                    JsonNode jsonNode = arrayNode.get(i);
                    if (jsonNode.has(keyToDelete) &&
                            jsonNode.get(keyToDelete).asText().equals(valueToDelete)) {

                        ((ArrayNode) rootNode.get("documents")).remove(i);
                        objectMapper.writeValue(jsonFile, rootNode);
                    }
                }
            }
        }
    }


    @Override
    public String updateJsonObject(File jsonFile, JsonProperty<?> oldProperty, JsonProperty<?> newProperty) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            if (!jsonFile.exists()) {
                return "JSON file not found.";
            }

            JsonNode rootNode = objectMapper.readTree(jsonFile);
            if (!rootNode.isArray()) {
                return "File content is not an array";
            }

            String oldKey = oldProperty.getKey();
            Object oldValue = oldProperty.getValue();


            String newKey = newProperty.getKey();
            Object newValue = newProperty.getValue();

            for (JsonNode jsonNode : rootNode) {
                if (jsonNode.has(oldKey) && jsonNode.get(oldKey).asText().equals(oldValue)) {
                    ((ObjectNode) jsonNode).set(newKey, (JsonNode) newValue); // Update the age property
                    break;
                }
            }

            // Write the updated JSON to the file
            objectMapper.writeValue(jsonFile, rootNode);

            return "Update Successful";
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred during JSON operations.";
        }
    }


    @Override
    public Optional<JsonNode> searchByProperty(File path, JsonProperty<?> jsonProperty) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(path.toURI());
        if (!jsonFile.exists()) {
            return Optional.empty();
        }

        JsonNode rootNode = objectMapper.readTree(jsonFile);
        if (!rootNode.isArray()) {
            return Optional.empty();
        }

        String key = jsonProperty.getKey();
        Object value = jsonProperty.getValue();

        for (JsonNode jsonNode : rootNode) {
            if (jsonNode.has(key) && jsonNode.get("city").asText().equals(value)) {
                return Optional.of(jsonNode);
            }
        }
        return Optional.empty();
    }
}
