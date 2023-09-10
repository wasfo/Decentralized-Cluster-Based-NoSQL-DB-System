package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@RequiredArgsConstructor
@Data

public class DeleteDocumentEvent extends WriteEvent {
    private String documentId;
    private String dbName;
    private String collectionName;
}
