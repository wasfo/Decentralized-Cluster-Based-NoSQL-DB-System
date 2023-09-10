package org.worker.api.writeRequests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.worker.api.WriteRequest;



@Data
@NoArgsConstructor
public class AddDocumentRequest extends WriteRequest {
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
    @NonNull
    private ObjectNode objectNode;
}
