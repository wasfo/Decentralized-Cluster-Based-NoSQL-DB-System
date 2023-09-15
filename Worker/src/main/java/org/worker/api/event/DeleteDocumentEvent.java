package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.DeleteDocumentRequest;

@NoArgsConstructor
@Data

public class DeleteDocumentEvent extends WriteEvent {
    private DeleteDocumentRequest request;

    public DeleteDocumentEvent(String username, DeleteDocumentRequest request) {
        this.request = request;
    }
}
