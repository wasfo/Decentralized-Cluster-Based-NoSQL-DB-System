package org.worker.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.worker.models.Collection;
import org.worker.models.JsonProperty;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface JsonService {
    <T> List<T> loadFromJson(String path, Class<T> clazz);

    public Optional<JsonNode> readArray(String filePath) throws IOException;
    public Optional<Collection> readArrayIntoCollection(String filePath) throws IOException;
    public void writeArray(String filePath, ArrayNode node) throws IOException;
    public void addJsonNodeToJsonArray(File jsonFile, ObjectNode newObject) throws IOException;
    public boolean deleteFirst(String filePath, JsonProperty<?> jsonProperty) throws IOException;
    public void deleteAll(String filePath, JsonProperty<?> jsonProperty) throws IOException;
    public String updateJsonObject(File jsonFile, JsonProperty<?> oldProperty, JsonProperty<?> newProperty);
    public Optional<JsonNode> searchByProperty(File JsonFile, JsonProperty<?> jsonProperty) throws IOException;
}
