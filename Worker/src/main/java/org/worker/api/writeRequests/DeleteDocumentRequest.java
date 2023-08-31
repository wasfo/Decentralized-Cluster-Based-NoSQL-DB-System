package org.worker.api.writeRequests;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.APIRequest;

@Data
@RequiredArgsConstructor
public class DeleteDocumentRequest extends APIRequest {

    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
}
