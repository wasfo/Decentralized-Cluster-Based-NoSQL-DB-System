package org.worker.broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public BroadcastService(List<Node> nodes) {
        this.nodes = nodes;
    }
    public void broadCast(APIRequest request, UserCredentials userCredentials, String endpoint) {

        HttpHeaders headers = new HttpHeaders();
        String username = userCredentials.getUsername();
        String password = userCredentials.getPassword();
        headers.setBasicAuth(username, password);
        HttpEntity<APIRequest> entity = new HttpEntity<>(request, headers);
        for (Node node : nodes) {
            String url = getUrl(node.getHostname(), node.getPort(), endpoint);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            logger.info(response.getBody());
        }

    }
    public String getUrl(String hostname, int port, String endPoint) {
        return "http://localhost:" + port + "/" + hostname + "/" + endPoint;
    }
}
