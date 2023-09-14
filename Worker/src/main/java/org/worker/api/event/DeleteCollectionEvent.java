package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.DeleteCollectionRequest;

@Data
public class DeleteCollectionEvent extends WriteEvent {

    private DeleteCollectionRequest request;

    public DeleteCollectionEvent(String username, DeleteCollectionRequest request) {
        super.username = username;
        this.request = request;
    }
}
