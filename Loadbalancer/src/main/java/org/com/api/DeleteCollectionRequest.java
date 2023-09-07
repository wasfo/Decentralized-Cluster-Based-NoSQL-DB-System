package org.com.api;



import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DeleteCollectionRequest extends APIRequest {

    @NotNull
    private String dbName;
    @NonNull
    private String collectionName;

}
