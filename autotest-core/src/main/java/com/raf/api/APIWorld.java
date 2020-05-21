package com.raf.api;

import com.raf.config.ConfigProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class APIWorld {
    public FilterableRequestSpecification requestSpecification;
    public Response response;
    public HashMap<String, String> customDataHolder = new HashMap<>();
    public String customResponseBody;
    public int customStatusCode;

    @Autowired
    ConfigProvider configProvider;

    public APIWorld() {
        requestSpecification = (FilterableRequestSpecification) RestAssured.with();
        response = null;
    }

    public void setBaseURI(String baseURI) {
        requestSpecification.baseUri(baseURI);
        requestSpecification.relaxedHTTPSValidation();
        requestSpecification.headers("Accept", "application/json");
    }

    public void setHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (requestSpecification.getHeaders().hasHeaderWithName(entry.getKey())) {
                requestSpecification.replaceHeader(entry.getKey(), entry.getValue());
            } else {
                requestSpecification.header(entry.getKey(), entry.getValue());
            }
        }
    }
}
