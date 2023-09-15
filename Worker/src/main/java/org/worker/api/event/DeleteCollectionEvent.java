package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.DeleteCollectionRequest;

@Data
@NoArgsConstructor
public class DeleteCollectionEvent extends WriteEvent {

    private DeleteCollectionRequest request;

    public DeleteCollectionEvent(String username, DeleteCollectionRequest request) {
        super.username = username;
        this.request = request;
    }
}
