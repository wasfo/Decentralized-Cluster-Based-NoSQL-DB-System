package org.worker.api.writeRequests;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@RequiredArgsConstructor
public class DeleteDocumentRequest extends WriteRequest {

    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
}
