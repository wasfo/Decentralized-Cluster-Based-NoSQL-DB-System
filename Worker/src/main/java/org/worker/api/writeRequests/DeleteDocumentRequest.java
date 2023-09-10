package org.worker.api.writeRequests;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;

@Data
@NoArgsConstructor
public class DeleteDocumentRequest extends WriteRequest {

    @NotNull
    private String docId;
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;


}
