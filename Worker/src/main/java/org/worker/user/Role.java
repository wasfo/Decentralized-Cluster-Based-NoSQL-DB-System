package org.worker.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.worker.deserializers.RoleDeserializer;

@JsonDeserialize(using = RoleDeserializer.class)
public enum Role {
    USER,
    ADMIN,
    UNKNOWN
}