package org.worker.api.event;

import lombok.Data;
import org.worker.api.writeRequests.IndexRequest;

@Data
public class IndexEvent extends WriteEvent {
    private IndexRequest indexRequest;

    public IndexEvent() {
    }

    public IndexEvent(String username, IndexRequest indexRequest) {
        super.username = username;
        this.indexRequest = indexRequest;
    }
}
