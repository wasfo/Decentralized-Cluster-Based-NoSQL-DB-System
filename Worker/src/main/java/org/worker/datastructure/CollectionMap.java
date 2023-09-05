package org.worker.datastructure;

import lombok.Data;

import java.util.HashMap;

@Data
public class CollectionMap {
    private HashMap<String, FieldValueMap> map;

    public CollectionMap() {
        this.map = new HashMap<>();
    }

}