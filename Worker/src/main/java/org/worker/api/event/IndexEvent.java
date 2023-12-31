package org.worker.api.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.worker.api.writeRequests.IndexRequest;

@Data
@NoArgsConstructor
public class IndexEvent extends WriteEvent {
    private IndexRequest indexRequest;


    public IndexEvent(String username, IndexRequest indexRequest) {
        super.username = username;
        this.indexRequest = indexRequest;
    }
}
