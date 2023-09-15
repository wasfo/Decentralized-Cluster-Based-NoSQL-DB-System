package org.worker.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;


public class SchemaValidator {

    public static boolean isValidDocument(JsonNode schemaNode, JsonNode jsonNode) throws ProcessingException {
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
        JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
        try {
            ProcessingReport processingReport = schema.validate(jsonNode);
            return processingReport.isSuccess();
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
