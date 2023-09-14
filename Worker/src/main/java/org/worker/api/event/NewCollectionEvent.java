package org.worker.api.event;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;
import org.worker.api.writeRequests.NewCollectionRequest;
import org.worker.models.Collection;

@Data
public class NewCollectionEvent extends WriteEvent {


    @NotNull
    private NewCollectionRequest request;

    public NewCollectionEvent(String username, NewCollectionRequest request) {
        super.username = username;
        this.request = request;
    }
}
