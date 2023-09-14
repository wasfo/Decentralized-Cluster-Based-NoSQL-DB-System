package org.worker.api.event;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.writeRequests.CreateDatabaseRequest;

@RequiredArgsConstructor
@Data

public class CreateDatabaseEvent extends WriteEvent {
    private CreateDatabaseRequest request;

    public CreateDatabaseEvent(String username, CreateDatabaseRequest request) {
        super.username = username;
        this.request = request;
    }
}
