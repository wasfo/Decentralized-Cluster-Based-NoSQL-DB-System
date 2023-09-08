package org.worker.api.writeRequests;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.APIRequest;
import org.worker.models.Collection;

@Data
@RequiredArgsConstructor
public class NewEmptyCollectionRequest extends APIRequest {
    private String collectionName;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
