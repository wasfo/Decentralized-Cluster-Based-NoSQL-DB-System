package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.models.Collection;

@Data
@RequiredArgsConstructor
public class NewCollectionEvent extends WriteEvent{
    private Collection collection;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
