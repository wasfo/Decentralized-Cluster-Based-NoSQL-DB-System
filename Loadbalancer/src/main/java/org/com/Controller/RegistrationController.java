package org.com.Controller;


import org.com.api.APIRequest;
import org.com.models.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.com.url.Urls.registrationUrl;

@RestController
@RequestMapping("/reg")
public class RegistrationController {

    private RestTemplate restTemplate;
    public RegistrationController(@Qualifier("registerTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String url = "http://NODE" + registrationUrl;
        HttpEntity<User> entity = new HttpEntity<>(user);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.DELETE,
                entity,
                String.class);

        return response;
    }
}
