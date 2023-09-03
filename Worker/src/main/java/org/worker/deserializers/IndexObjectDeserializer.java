package org.worker.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class IndexObjectDeserializer extends JsonDeserializer<IndexObject> {
    @Override
    public IndexObject deserialize(JsonParser jsonParser,
                                   DeserializationContext deserializationContext) throws IOException {
//        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//        String collectionName = node.get("collectionName").asText();
//        String fieldName = node.get("fieldName").asText();
//        String value = node.get("value").asText();
//        return new IndexObject(collectionName, fieldName, value);
        return null;
    }
}
