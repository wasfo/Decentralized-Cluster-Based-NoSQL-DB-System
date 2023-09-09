package org.worker.api.readRequests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;



@RequiredArgsConstructor
@Data
public class ReadCollectionRequest extends WriteRequest {

    @NotNull
    private String dbName;
    @NotNull
    private String collectionName;

}
