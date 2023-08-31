package org.worker.deserializers;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class HashMapKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        String[] parts = key.substring(1, key.length() - 1).split(", ");
        String collectionName = parts[0];
        String fieldName = parts[1];
        String value = parts[2];
        return new IndexObject(collectionName, fieldName, value);
    }
}
