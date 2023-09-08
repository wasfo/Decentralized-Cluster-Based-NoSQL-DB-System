package org.com.Controller;


import org.com.api.APIRequest;
import org.com.api.DeleteCollectionRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static org.com.url.Urls.baseCollectionUrl;

@RestController
@RequestMapping("/api/cluster")
public class LoginController {

    private RestTemplate restTemplate;

    private String nodeUrl = "http://NODE";

    public LoginController(@Qualifier("loginTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/getConnection")
    public void getConnection(@RequestHeader HttpHeaders headers) {

        String url = nodeUrl;
        HttpEntity<APIRequest> entity = new HttpEntity<>( headers);
        restTemplate.exchange(url,
                HttpMethod.DELETE,
                entity,
                Void.class);

    }
}
