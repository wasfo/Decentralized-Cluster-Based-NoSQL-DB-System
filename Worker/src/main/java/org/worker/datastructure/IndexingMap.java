package org.worker.datastructure;

import lombok.Data;
import org.worker.models.Document;
import org.worker.models.JsonProperty;

import java.util.*;

public class IndexingMap {
    private final CollectionMap collectionMap;
    private final DatabasesMap databasesMap;
    private final FieldValueMap fieldValueMap;

    public IndexingMap() {
        this.collectionMap = new CollectionMap();
        this.databasesMap = new DatabasesMap();
        this.fieldValueMap = new FieldValueMap();
    }


    public void putDatabase(String databaseName) {
        if (!databasesMap.getMap().containsKey(databaseName))
            databasesMap.getMap().put(databaseName, new CollectionMap());
    }

    public void putCollection(String databaseName, String collectionName) {
        if (!databasesMap.getMap().get(databaseName).getMap().containsKey(collectionName))
            getCollectionsMap(databaseName).getMap().put(collectionName, new FieldValueMap());

    }

    public void putDocuments(String databaseName,
                             String collectionName,
                             Map.Entry<String, Object> entry,
                             List<String> documentIds) {

        getCollectionsMap(databaseName)
                .getMap()
                .get(collectionName)
                .getMap().put(entry, documentIds);


    }

    public CollectionMap getDatabasesMap(String databaseName) {
        return databasesMap.getMap().get(databaseName);
    }

    public CollectionMap getCollectionsMap(String databaseName) {
        return databasesMap.getMap().get(databaseName);
    }

    public FieldValueMap getFieldValueMap(String collectionName) {
        return collectionMap.getMap().get(collectionName);
    }

    public List<String> getDocumentIds(Map.Entry<String, Object> indexedEntry) {
        return fieldValueMap.getMap().get(indexedEntry);
    }


}
