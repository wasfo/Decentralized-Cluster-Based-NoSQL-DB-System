package org.worker.services;

import org.worker.deserializers.IndexObject;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IndexingService {

    public ResponseEntity<String> createIndex(String userDir,
                                              String dbName,
                                              String collectionName,
                                              String fieldName) throws IOException;

    public void removeIndex(String userDir,
                            String dbName,
                            String collectionName,
                            String fieldName) throws IOException;

    public Optional<List<String>> readSpecificIndex(String userDir,
                                                    IndexObject targetObject) throws IOException;


    public boolean loadIndexToMap(String username,
                                  String dbName,
                                  String collectionName,
                                  String fieldName);

    public void loadAllIndexesToMap(String username);

}
