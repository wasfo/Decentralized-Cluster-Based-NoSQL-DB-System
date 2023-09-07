package org.com.api;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DeleteDocumentRequest extends APIRequest {

    @NonNull
    private String dbName;
    @NonNull
    private String collectionName;
}
