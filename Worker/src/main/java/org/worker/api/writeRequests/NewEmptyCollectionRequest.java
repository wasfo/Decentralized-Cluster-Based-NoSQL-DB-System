package org.worker.api.writeRequests;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@NoArgsConstructor
public class NewEmptyCollectionRequest extends WriteRequest {
    private String collectionName;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
