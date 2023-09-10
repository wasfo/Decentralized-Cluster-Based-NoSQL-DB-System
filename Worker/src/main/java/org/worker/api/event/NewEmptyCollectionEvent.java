package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@RequiredArgsConstructor
@Data
public class NewEmptyCollectionEvent extends WriteEvent {
    private String collectionName;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;

    private String username;
}
