package org.worker.api.writeRequests;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.APIRequest;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DeleteCollectionRequest extends APIRequest {

    @NotNull
    private String dbName;
    @NonNull
    private String collectionName;

}
