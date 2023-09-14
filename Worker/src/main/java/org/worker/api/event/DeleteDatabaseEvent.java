package org.worker.api.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.writeRequests.DeleteDatabaseRequest;

@RequiredArgsConstructor
@Data

public class DeleteDatabaseEvent extends WriteEvent {
    private DeleteDatabaseRequest request;

    public DeleteDatabaseEvent(String username, DeleteDatabaseRequest request) {
        super.username = username;
        this.request = request;
    }
}




