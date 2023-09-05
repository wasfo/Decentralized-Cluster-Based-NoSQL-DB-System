package org.worker.datastructure;

import lombok.Data;

import java.util.HashMap;


@Data
public class DatabasesMap {
    private HashMap<String, CollectionMap> map;

    public DatabasesMap() {
        this.map = new HashMap<>();
    }
}