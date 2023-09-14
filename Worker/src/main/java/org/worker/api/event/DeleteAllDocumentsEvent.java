package org.worker.api.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.writeRequests.DeleteAllDocumentsRequest;
import org.worker.models.JsonProperty;

@Data
@RequiredArgsConstructor
public class DeleteAllDocumentsEvent<T> extends WriteEvent {

    private DeleteAllDocumentsRequest<T> request;

    public DeleteAllDocumentsEvent(String username, DeleteAllDocumentsRequest<T> request) {
        super.username = username;
        this.request = request;
    }
}
