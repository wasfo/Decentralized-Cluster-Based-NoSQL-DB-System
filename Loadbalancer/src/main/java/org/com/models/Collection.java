package org.com.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    @Setter
    @Getter
    private String collectionName;
    @Getter
    @Setter
    private List<Document> documents;

    public Collection() {
    }

    public Collection(String collectionName) {
        this.collectionName = collectionName;

    }

    public void addDocument(Document document) {
        if (documents == null)
            documents = new ArrayList<>();
        documents.add(document);
    }


    @Override
    public String toString() {
        String jsonString = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.out.println("toString issue");
        }
        return jsonString;
    }


}



