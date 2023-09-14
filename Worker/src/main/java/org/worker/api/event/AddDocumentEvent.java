package org.worker.api.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.AddDocumentRequest;


@RequiredArgsConstructor
@Data
public class AddDocumentEvent extends WriteEvent {

    private AddDocumentRequest request;

    public AddDocumentEvent(String username, AddDocumentRequest request) {
        super.username = username;
        this.request = request;
    }
}
