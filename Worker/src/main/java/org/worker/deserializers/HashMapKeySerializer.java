package org.worker.deserializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class HashMapKeySerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof IndexObject) {
            // Convert IndexObject to a string representation and use it as the key
            String key = ((IndexObject) value).toString();
            gen.writeFieldName(key);
        } else {
            throw new IllegalArgumentException("Unexpected key type: " + value.getClass().getName());
        }
    }
}
