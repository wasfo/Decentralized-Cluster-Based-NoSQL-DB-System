package org.worker.deserializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class IndexObjectSerializer extends JsonSerializer<IndexObject> {
    @Override
    public void serialize(IndexObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("collectionName", value.getCollectionName());
        gen.writeStringField("fieldName", value.getFieldName());
        gen.writeStringField("value", value.getValue());
        gen.writeEndObject();
    }
}
