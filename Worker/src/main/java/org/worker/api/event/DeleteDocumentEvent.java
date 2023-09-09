package org.worker.api.event;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@RequiredArgsConstructor
public class DeleteDocumentEvent extends WriteEvent {

    private String dbName;
    private String collectionName;
}
