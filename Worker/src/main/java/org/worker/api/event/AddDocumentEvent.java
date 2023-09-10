package org.worker.api.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;


@RequiredArgsConstructor
@Data
public class AddDocumentEvent extends WriteEvent {

    private String dbName;

    private String collectionName;

    private ObjectNode objectNode;
}
