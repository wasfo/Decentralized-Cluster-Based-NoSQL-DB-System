package org.worker.api.readRequests;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.worker.models.JsonProperty;

@Data
@NoArgsConstructor
public class ReadDocumentByCriteriaRequest {

    private String dbName;
    private String colName;
    private JsonProperty<?> criteria;
}
