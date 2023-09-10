package org.worker.api.writeRequests;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;

@Data
@NoArgsConstructor

public class DeleteCollectionRequest extends WriteRequest {

    @NotNull
    private String dbName;
    @NonNull
    private String collectionName;

}
