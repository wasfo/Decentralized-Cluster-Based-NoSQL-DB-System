package org.worker.api.writeRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.worker.api.WriteRequest;

@Data
public class DeleteDatabaseRequest extends WriteRequest {
    @NotNull
    @NotBlank
    private String dbName;
}
