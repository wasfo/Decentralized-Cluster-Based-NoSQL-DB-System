package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.NewCollectionRequest;
import org.worker.models.Collection;

@Data
@NoArgsConstructor
public class NewCollectionEvent extends WriteEvent {


    @NotNull
    private NewCollectionRequest request;

    public NewCollectionEvent(String username, NewCollectionRequest request) {
        super.username = username;
        this.request = request;
    }
}
