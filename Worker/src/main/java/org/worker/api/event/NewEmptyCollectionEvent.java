package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@RequiredArgsConstructor
public class NewEmptyCollectionEvent extends WriteEvent {
    private String collectionName;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
