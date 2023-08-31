package org.worker.services;

import org.worker.deserializers.IndexObject;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface IndexingService {

    public ResponseEntity<String> createIndex(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String fieldName) throws IOException;
    public void removeIndex(String userDir,
                            String dbName,
                            String collectionName,
                            String fieldName);

    public HashMap<IndexObject, List<String>> readIndex(String userDir,
                                                        String dbName,
                                                        String collectionName,
                                                        String fieldName) throws IOException;
}
