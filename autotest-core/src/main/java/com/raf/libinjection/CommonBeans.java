package com.raf.libinjection;

import com.raf.support.RAFWorld;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Configuration
public class CommonBeans {

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public RAFWorld initRafWorld() {
        log.atTrace().log("[RAF] CommonBeans -> init RafWorld ");
        return new RAFWorld();
    }
}
