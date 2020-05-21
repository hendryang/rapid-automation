package com.raf.api;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;


public class CommonAPI {
    @Autowired
    public APIWorld apiWorld;

    public void sendGetRequest(String path, Map<String, String> pathParams, Map<String, String> queryParams) {
        clearParams(pathParams, queryParams);

        apiWorld.requestSpecification.pathParams(pathParams);
        apiWorld.requestSpecification.queryParams(queryParams);
        apiWorld.response = apiWorld.requestSpecification.basePath(path).get();
    }

    public void sendPostRequest(String path, String body) {
        apiWorld.response = apiWorld.requestSpecification.basePath(path).body(body).post();
    }

    public void sendPutRequest(String path, Map<String, String> pathParams, String body) {
        clearParams(pathParams, Collections.emptyMap());

        apiWorld.requestSpecification.pathParams(pathParams);
        apiWorld.response = apiWorld.requestSpecification.basePath(path).body(body).put();
    }

    public void sendDeleteRequest(String path, Map<String, String> pathParams) {
        clearParams(pathParams, Collections.emptyMap());

        apiWorld.requestSpecification.pathParams(pathParams);
        apiWorld.response = apiWorld.requestSpecification.basePath(path).delete();
    }

    private void clearParams(Map<String, String> pathParams, Map<String, String> queryParams) {
        for (String pathParamKey : pathParams.keySet()) {
            apiWorld.requestSpecification.removePathParam(pathParamKey);
        }

        for (String queryParamKey : queryParams.keySet()) {
            apiWorld.requestSpecification.removeQueryParam(queryParamKey);
        }
    }
}
