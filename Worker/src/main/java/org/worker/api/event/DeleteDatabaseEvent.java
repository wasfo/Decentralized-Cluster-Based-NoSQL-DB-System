package org.worker.api.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data

public class DeleteDatabaseEvent extends WriteEvent {
    private String databaseName;
}




