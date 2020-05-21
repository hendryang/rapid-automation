package com.raf.libinjection;

import com.raf.config.ConfigProvider;
import com.raf.ui.component.PageAction;
import com.raf.ui.driver.BrowserFactory;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.TimeUnit;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Configuration
public class UIBeans {
    @Autowired
    private ConfigProvider configProvider;
    private BrowserFactory browserFactory;

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public BrowserFactory initializeBrowserFactory() {
        String browser = configProvider.getUiSeleniumBrowser();
        String method = configProvider.getUiSeleniumMethod();
        String remoteURL = configProvider.getUiSeleniumRemoteURL();
        boolean isVideoRecording = configProvider.isUiSeleniumRecording();
        browserFactory = new BrowserFactory(browser, method, remoteURL, isVideoRecording);
        return browserFactory;
    }

    @Bean(destroyMethod = "quit")
    @DependsOn("initializeBrowserFactory")
    @Scope(SCOPE_CUCUMBER_GLUE)
    public WebDriver provideWebDriver() {
        log.atTrace().log("[RAF] UIBeans -> init WebDriver ");
        WebDriver webDriver = browserFactory.getWebDriver();
        webDriver.manage().timeouts().implicitlyWait(configProvider.getDefaultImplicitTimeout(), TimeUnit.SECONDS);
        return webDriver;
    }

    @Bean
    @DependsOn("provideWebDriver")
    @Scope(SCOPE_CUCUMBER_GLUE)
    public PageAction initPageActionPOC() {
        log.atTrace().log("[RAF] UIBeans -> init PageAction ");
        return new PageAction();
    }
}