package org.worker.api.readRequests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.APIRequest;



@RequiredArgsConstructor
@Data
public class ReadCollectionRequest extends APIRequest {

    @NotNull
    private String dbName;
    @NotNull
    private String collectionName;

}
