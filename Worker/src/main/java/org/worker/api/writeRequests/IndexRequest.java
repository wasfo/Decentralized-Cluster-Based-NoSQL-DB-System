package org.worker.api.writeRequests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class IndexRequest {

    private String dbName;
    private String collectionName;
    private String fieldName;
}
