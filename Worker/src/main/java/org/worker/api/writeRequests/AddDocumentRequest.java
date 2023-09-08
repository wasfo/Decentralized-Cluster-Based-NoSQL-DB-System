package org.worker.api.writeRequests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.APIRequest;
import org.worker.models.Document;


@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class AddDocumentRequest extends APIRequest {
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
    @NonNull
    private ObjectNode objectNode;
}
