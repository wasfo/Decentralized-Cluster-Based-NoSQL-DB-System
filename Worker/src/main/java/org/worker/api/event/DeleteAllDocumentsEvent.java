package org.worker.api.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.worker.api.writeRequests.DeleteAllDocumentsRequest;
import org.worker.models.JsonProperty;

@Data
@NoArgsConstructor
public class DeleteAllDocumentsEvent<T> extends WriteEvent {

    private DeleteAllDocumentsRequest<T> request;

    public DeleteAllDocumentsEvent(String username, DeleteAllDocumentsRequest<T> request) {
        super.username = username;
        this.request = request;
    }
}
