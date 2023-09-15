package org.worker.api.readRequests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadDocumentByIdRequest {

    private String dbName;
    private String colName;
    private String docId;
}
