package org.com;

import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class Test {


    public static void main(String[] args) throws InterruptedException {
        //
        ResponseEntity<String> loginResponse = requestWithoutSession();

        System.out.println(loginResponse);
        System.out.println("-----------------------------------");
        System.out.println("headers -> " + loginResponse.getHeaders());

//        Thread.sleep(10000);
//        System.out.println(requestWithSession(headers));
    }

    public static ResponseEntity<String> requestWithoutSession() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ahmad", "12321");
        String loginUrl = "http://localhost:8082/login";
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> loginResponse = restTemplate.
                exchange(loginUrl, HttpMethod.GET, requestEntity, String.class);

        return loginResponse;
    }

    public static ResponseEntity<String> requestWithSession(HttpHeaders headers) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8082/admin/protected";
        // Extract the session cookie from the response headers
        HttpHeaders newHeaders = new HttpHeaders();
        List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
        String sessionCookie = null;
        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID=")) {
                sessionCookie = cookie;
                break;
            }
        }
        newHeaders.set(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<String> requestEntity = new HttpEntity<>(newHeaders);
        ResponseEntity<String> response = restTemplate.
                exchange(resourceUrl, HttpMethod.GET, requestEntity, String.class);

        return response;
    }
}
