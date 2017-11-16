package com.financeapp.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Matt on 16/11/2017.
 */
public class RequestTestUtils {

    private String accessToken;
    private TestRestTemplate restTemplate;

    public RequestTestUtils(String accessToken, TestRestTemplate restTemplate) {
        this.accessToken = accessToken;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> sendAuthenticatedRequest(Object body, HttpMethod method, String url, Class clazz) throws URISyntaxException {
        URI uri = new URI(url);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        RequestEntity requestEntity = new RequestEntity(body, headers, method, uri);
        ResponseEntity responseEntity = restTemplate.exchange(requestEntity, clazz);

        return responseEntity;
    }
}
