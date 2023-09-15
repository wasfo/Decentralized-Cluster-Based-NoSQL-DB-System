package org.worker.api.event;


import lombok.Builder;
import lombok.Data;

@Data

public abstract class WriteEvent {
    protected String username;
}
