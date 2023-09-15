package org.worker.api.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.AddDocumentRequest;


@NoArgsConstructor
@Data
public class AddDocumentEvent extends WriteEvent {

    private AddDocumentRequest request;

    public AddDocumentEvent(String username, AddDocumentRequest request) {
        super.username = username;
        this.request = request;
    }
}
