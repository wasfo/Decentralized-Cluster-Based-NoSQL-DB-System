package org.com.Controller;

import org.com.api.APIRequest;
import org.com.api.DeleteCollectionRequest;
import org.com.api.NewCollectionRequest;
import org.com.models.Role;
import org.com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.com.url.Urls.*;

@RestController
@RequestMapping("/cluster/api")
public class WriteController {

    private RestTemplate restTemplate;

    private final String nodeUrl = "http://NODE";


    @Autowired
    public WriteController(@Qualifier("writeTemplate") RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;

    }

    @GetMapping("/hello")
    public void hello() {
        String url = nodeUrl + "/admin/hello";
        ResponseEntity<?> response = restTemplate.getForEntity(url, String.class);
    }

    @PostMapping("/collections/delete")
    public void deleteCollectionRequest(@RequestBody DeleteCollectionRequest deleteCollectionRequest,
                                        @RequestHeader HttpHeaders headers) {

        String url = nodeUrl + baseCollectionUrl;
        HttpEntity<APIRequest> entity = new HttpEntity<>(deleteCollectionRequest, headers);
        restTemplate.exchange(url,
                HttpMethod.DELETE,
                entity,
                Void.class);

    }

    @PostMapping("/collections/write")
    public void writeCollectionRequest(@RequestBody NewCollectionRequest apiRequest,
                                       @RequestHeader HttpHeaders headers) {

        String url = nodeUrl + baseCollectionUrl + "/new";

        HttpEntity<APIRequest> entity = new HttpEntity<>(apiRequest, headers);
        restTemplate.exchange(url,
                HttpMethod.POST,
                entity,
                Void.class);
    }

    @PostMapping("/database/create")
    public void createDatabase(@RequestBody String dbName,
                               @RequestHeader HttpHeaders headers) {

        String url = nodeUrl + createDatabaseUrl;

        HttpEntity<String> entity = new HttpEntity<>(dbName, headers);
        restTemplate.exchange(url,
                HttpMethod.POST,
                entity,
                Void.class);
    }

}
