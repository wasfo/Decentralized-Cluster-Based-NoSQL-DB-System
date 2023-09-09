package org.worker.api.writeRequests;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@RequiredArgsConstructor
public class DeleteCollectionRequest extends WriteRequest {

    @NotNull
    private String dbName;
    @NonNull
    private String collectionName;

}
