package org.worker.api.event;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@RequiredArgsConstructor
@Builder

public class DeleteDocumentEvent extends WriteEvent {

    private String dbName;
    private String collectionName;
}
