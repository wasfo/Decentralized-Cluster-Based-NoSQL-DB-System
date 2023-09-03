package org.worker.api;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class IndexRequest {

    private String dbName;
    private String collectionName;
    private String fieldName;
}
