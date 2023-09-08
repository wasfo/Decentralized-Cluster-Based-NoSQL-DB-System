package org.com.client;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class Client {

    private String username;
    private String password;
    private String nodePort;

    private String baseNodeUrl = "http://localhost:" + nodePort;

    private final RestTemplate restTemplate = new RestTemplate();

    public void setCredentials(String username,
                               String password) {
        this.username = username;
        this.password = password;
    }

    public boolean getConnection() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        headers.setBasicAuth(username, password);
        String loadBalancerUrl = "http://localhost:8087/api/cluster/getConnection";
        ResponseEntity<String> response = restTemplate.exchange(loadBalancerUrl,
                HttpMethod.POST,
                entity,
                String.class);

        if (response.getStatusCode().equals(HttpStatus.OK) && !Objects.requireNonNull(response.getBody()).isEmpty()) {
            nodePort = response.getBody();
            return true;
        }
        return false;
    }

    public boolean connect(CollectionRequest collectionRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<CollectionRequest> entity = new HttpEntity<>(collectionRequest, headers);
        String nodeUrl = baseNodeUrl + "/some end point";
        restTemplate.exchange(nodeUrl,
                HttpMethod.POST,
                entity,
                Void.class);

    }
}
