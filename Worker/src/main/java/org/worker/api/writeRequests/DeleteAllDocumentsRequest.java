package org.worker.api.writeRequests;


import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.models.JsonProperty;

@Data
@NoArgsConstructor

public class DeleteAllDocumentsRequest<T> extends WriteRequest {

    private JsonProperty<T> criteria;
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
}
