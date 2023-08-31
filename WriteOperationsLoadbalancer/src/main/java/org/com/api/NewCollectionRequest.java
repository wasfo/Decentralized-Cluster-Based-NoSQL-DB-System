package org.com.api;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.com.models.Collection;

@Data
@RequiredArgsConstructor
public class NewCollectionRequest extends APIRequest {
    @NotNull
    private Collection collection;
    @NotNull
    private ObjectNode schema;
    @NotNull
    private String dbName;
}
