package org.worker.api.writeRequests;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@RequiredArgsConstructor
public class NewEmptyCollectionRequest extends WriteRequest {
    private String collectionName;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}