package org.worker.api.event;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
public class DeleteCollectionEvent extends WriteEvent {
    private String dbName;
    private String collectionName;

}
