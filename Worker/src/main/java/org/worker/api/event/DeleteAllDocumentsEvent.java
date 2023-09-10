package org.worker.api.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.models.JsonProperty;

@Data
@RequiredArgsConstructor
public class DeleteAllDocumentsEvent<T> extends WriteEvent {
    
    private JsonProperty<T> criteria;
    private String dbName;
    private String collectionName;
}
