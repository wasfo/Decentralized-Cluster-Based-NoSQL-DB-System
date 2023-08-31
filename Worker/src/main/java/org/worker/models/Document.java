package org.worker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    @Getter
    @Setter
    private String _id;
    @Getter
    @Setter
    private ObjectNode objectNode;
    private final ObjectMapper mapper = new ObjectMapper();

    public Document(ObjectNode objectNode) {
        this._id = objectNode.get("_id").asText();
        this.objectNode = objectNode;
    }

    private Document() {
        this.objectNode = mapper.createObjectNode();
        this._id = UUID.randomUUID().toString();
    }

    public static Document createEmptyDocument() {
        return new Document();
    }

    public void put(String field, Object value) {
        objectNode.put(field, String.valueOf(value));
    }

    public static Document toDocument(ObjectNode objectNode) {
        return new Document(objectNode);
    }

    public static List<Document> toDocuments(ArrayNode nodes) throws JsonProcessingException {
        List<Document> documentList = new ArrayList<>();
        for (JsonNode node : nodes) {
            Document document = toDocument((ObjectNode) node);
            documentList.add(document);
        }
        return documentList;
    }

    @Override
    public String toString() {
        String jsonString = null;
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.out.println("toString issue");
        }
        return jsonString;
    }


}
