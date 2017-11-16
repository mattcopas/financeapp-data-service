package com.financeapp.utils;

import com.financeapp.BaseTest;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.json.simple.JSONObject;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Created by Matt on 16/11/2017.
 */
public abstract class OAuth2TestUtils extends BaseTest {


    public static String getAccessToken(TestRestTemplate restTemplate) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.encode("financeapp:secret".getBytes()));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity tokenResponse = restTemplate.postForEntity(
                "/oauth/token?grant_type=password&username=test@test.com&password=password",
                entity,
                JSONObject.class
        );
        JSONObject tokenResponseBody = (JSONObject) tokenResponse.getBody();
        return (String) tokenResponseBody.get("access_token");
    }
}
