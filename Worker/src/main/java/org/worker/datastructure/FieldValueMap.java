package org.worker.datastructure;


import lombok.Data;
import org.worker.models.Document;
import org.worker.models.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FieldValueMap {
    private HashMap<Map.Entry<String, Object>, List<String>> map;

    public FieldValueMap() {
        this.map = new HashMap<>();
    }
}