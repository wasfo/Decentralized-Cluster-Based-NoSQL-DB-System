package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.DeleteDocumentRequest;

@RequiredArgsConstructor
@Data

public class DeleteDocumentEvent extends WriteEvent {
    private DeleteDocumentRequest request;

    public DeleteDocumentEvent(String username, DeleteDocumentRequest request) {
        this.request = request;
    }
}
