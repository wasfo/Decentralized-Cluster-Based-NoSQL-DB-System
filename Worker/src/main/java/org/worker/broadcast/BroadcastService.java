package org.worker.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.worker.api.WriteRequest;
import org.worker.api.event.WriteEvent;
import org.worker.node.Node;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * broadcast the change for each node.
 */
@Service
public class BroadcastService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private KafkaTemplate<String, WriteEvent> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(BroadcastService.class);
    private final List<Node> nodes;

    @Value("${node.name}")
    public String nodeName;

    @Autowired
    public BroadcastService(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void broadCastWithKafka(Topic topic, WriteEvent event) {
        kafkaTemplate.send(topic.getTopicValue(), event);
    }

    public void broadCastWithHttp(WriteRequest request,
                                  HttpHeaders headers,
                                  String endpoint,
                                  HttpMethod httpMethod) {

        HttpEntity<WriteRequest> entity = new HttpEntity<>(request, headers);
        for (Node node : nodes) {
            if (node.getHostname().equals(nodeName))
                continue;
            String url = getUrl(node.getHostname(), node.getPort(), endpoint);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    httpMethod,
                    entity,
                    String.class
            );
            logger.info(response.getBody());
        }

    }

    public String getUrl(String hostname, int port, String endPoint) {
        return "http://" + hostname + ":" + port + "/" + endPoint;
    }
}
