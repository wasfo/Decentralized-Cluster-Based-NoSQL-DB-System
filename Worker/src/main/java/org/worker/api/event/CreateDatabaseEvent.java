package org.worker.api.event;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.worker.api.writeRequests.CreateDatabaseRequest;


@Data
@NoArgsConstructor
public class CreateDatabaseEvent extends WriteEvent {
    private CreateDatabaseRequest request;

    public CreateDatabaseEvent(String username, CreateDatabaseRequest request) {
        super.username = username;
        this.request = request;
    }
}
