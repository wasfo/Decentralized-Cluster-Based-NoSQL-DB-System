package org.worker.api.event;


import lombok.Data;

@Data
public abstract class WriteEvent {
    protected boolean isBroadcasted = false;
    protected String username;
    protected String password;
}
