package org.worker.api.event;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateDatabaseEvent extends WriteEvent {
    private String databaseName;
}
