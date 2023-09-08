package org.worker.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.worker.api.APIRequest;
import org.worker.node.Node;
import org.worker.user.UserCredentials;

import java.util.List;



/**
 * broadcast the change for each node.
 */
@Service
public class BroadcastService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(BroadcastService.class);
    private final List<Node> nodes;

    @Value("${node.name}")
    public String nodeName;

    @Autowired
    public BroadcastService(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void broadCast(APIRequest request,
                          HttpHeaders headers,
                          String endpoint,
                          HttpMethod httpMethod) {

        request.setBroadcasted(true);
        HttpEntity<APIRequest> entity = new HttpEntity<>(request, headers);
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
