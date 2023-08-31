package org.worker.api;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.models.Collection;

@Data
@RequiredArgsConstructor
public class NewCollectionRequest extends APIRequest {
    private Collection collection;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
