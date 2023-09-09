package org.worker.api.writeRequests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;


@RequiredArgsConstructor
@Data
public class AddDocumentRequest extends WriteRequest {
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
    @NonNull
    private ObjectNode objectNode;
}
