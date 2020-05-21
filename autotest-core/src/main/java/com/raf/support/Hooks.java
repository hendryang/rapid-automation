package com.raf.support;

import com.raf.ui.driver.BrowserFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


@Log4j2
public class Hooks {
    private static final Logger logger = LogManager.getLogger(Hooks.class);

//    @Autowired
//    private WebDriver webDriver;

    @Autowired
    private BrowserFactory browserFactory;


    @AfterStep
    public void frameworkafterstep(Scenario scenario) {
        log.atTrace().log("After step of {} - {}", scenario.getName(), scenario.getLine());
    }

    //make sure this is the last @After we executed in Cucumber
    @After(order = 10000001)
    public void frameworkafter(Scenario scenario) {
        log.atTrace().log("After {} - {}", scenario.getName(), scenario.getLine());

        browserFactory.destroy(scenario);
    }
}