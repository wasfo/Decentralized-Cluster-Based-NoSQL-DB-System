package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.NewEmptyCollectionRequest;

@Data
@NoArgsConstructor
public class NewEmptyCollectionEvent extends WriteEvent {

    private NewEmptyCollectionRequest request;

    public NewEmptyCollectionEvent(String username, NewEmptyCollectionRequest request) {
        super.username = username;
        this.request = request;
    }
}
