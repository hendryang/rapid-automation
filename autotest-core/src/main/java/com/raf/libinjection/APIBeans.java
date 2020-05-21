package com.raf.libinjection;


import com.raf.api.APIUtils;
import com.raf.api.APIWorld;
import com.raf.api.CommonAPI;
import com.raf.config.ConfigProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Configuration
public class APIBeans {
    private static final Logger logger = LogManager.getLogger(APIBeans.class);

    @Autowired
    ConfigProvider configProvider;

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public APIUtils initAPIUtils() {
        log.atTrace().log("[RAF] APIBeans -> init APIUtils");
        return new APIUtils();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public CommonAPI initCommonAPI() {
        log.atTrace().log("[RAF] APIBeans -> init CommonAPI");
        return new CommonAPI();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    @Lazy
    public APIWorld provideAPIWorld() {
        log.atTrace().log("[RAF] APIBeans -> init APIWorld");
        APIWorld apiWorld = new APIWorld();
        apiWorld.setBaseURI(configProvider.getApiBaseUri());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");


        headers.put("Authorization", "tokenplaceholder");
        apiWorld.setHeaders(headers);
        return apiWorld;
    }
}
