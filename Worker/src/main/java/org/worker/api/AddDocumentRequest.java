package org.worker.api;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.worker.models.Document;


@RequiredArgsConstructor
@Data
public class AddDocumentRequest extends APIRequest {
    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
    @NonNull
    private Document document;
}
