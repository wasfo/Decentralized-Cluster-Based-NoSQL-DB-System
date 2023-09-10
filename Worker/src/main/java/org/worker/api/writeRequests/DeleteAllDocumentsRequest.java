package org.worker.api.writeRequests;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.models.JsonProperty;

@Data
@RequiredArgsConstructor
public class DeleteAllDocumentsRequest<T> extends WriteRequest {

    private JsonProperty<T> criteria;
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
}
