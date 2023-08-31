package org.worker.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.worker.models.Document;

public class SchemaValidator {

    static public boolean isValidDocument(JsonNode schemaNode, Document document) throws ProcessingException {
        if (document.getObjectNode().size() != schemaNode.size())
            return false;
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
        JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
        ProcessingReport processingReport = schema.validate(document.getObjectNode());
        return processingReport.isSuccess();
    }


}
