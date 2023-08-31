package org.worker.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.worker.user.Role;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {


    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String enumValue = p.getText();
        try {
            return Role.valueOf(enumValue);
        } catch (IllegalArgumentException e) {
            return Role.UNKNOWN; // Replace with your handling logic
        }
    }
}
