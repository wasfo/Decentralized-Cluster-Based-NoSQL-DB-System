package org.worker.api.writeRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.worker.api.WriteRequest;

@Data
@NoArgsConstructor

public class DeleteDatabaseRequest extends WriteRequest {
    @NotNull
    @NotBlank
    private String dbName;
}
