package org.com.api;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.com.models.Document;


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
